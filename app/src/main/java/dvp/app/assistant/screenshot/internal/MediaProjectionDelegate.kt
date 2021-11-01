package dvp.app.assistant.screenshot.internal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.Surface
import dvp.app.assistant.screenshot.Result
import dvp.app.assistant.screenshot.ScreenshotResult
import dvp.app.assistant.screenshot.internal.Utils.checkOnMainThread
import dvp.app.assistant.screenshot.internal.Utils.closeSafely
import dvp.app.assistant.screenshot.internal.Utils.interruptSafely
import dvp.app.assistant.screenshot.internal.Utils.releaseSafely
import dvp.app.assistant.screenshot.internal.Utils.stopSafely
import java.lang.ref.WeakReference

internal class MediaProjectionDelegate(
    activity: Activity,
    private val permissionRequestCode: Int
) {

    private val projectionManager = activity.getMediaProjectionManager()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var pendingResult: ScreenshotResultImpl? = null
    private var captureThread: HandlerThread? = null

    private val activityReference = WeakReference(activity)
    private val screenshotSpec by lazy { ScreenshotSpec(activity) }

    init {
        val act = activityReference.get() ?: throw Exception("activity null")
        act.startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            permissionRequestCode
        )
    }

    fun shot(): ScreenshotResult {
        checkOnMainThread()
        val result = pendingResult
        if (result != null) {
            return result
        }

        val newResult = ScreenshotResultImpl()

        val projection = LAST_ACCESS_DATA?.let(::getMediaProjection)!!
        captureInBackground(projection, screenshotSpec)

        return newResult.also {
            pendingResult = it
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == permissionRequestCode) {
            LAST_ACCESS_DATA = ProjectionAccessData(resultCode, data)
        }
    }

    private fun captureInBackground(
        projection: MediaProjection,
        screenshotSpec: ScreenshotSpec,
        delayMs: Long = 0L
    ) {
        val captureThread = startCaptureThread()
        val captureThreadHandler = Handler(captureThread.looper)

        captureThreadHandler.postDelayed({
            doCapture(projection, screenshotSpec, captureThreadHandler)
        }, delayMs)
    }

    private fun doCapture(projection: MediaProjection, spec: ScreenshotSpec, handler: Handler) {
        var imageReader: ImageReader? = null
        var virtualDisplay: VirtualDisplay? = null
        try {
            imageReader = createImageReader(projection, spec, handler)
            virtualDisplay = createVirtualDisplay(projection, imageReader.surface, spec, handler)
            val callback = ReleaseOnStopCallback(projection, imageReader, virtualDisplay)
            projection.registerCallback(callback, handler)
        } catch (e: Exception) {
            releaseSafely(virtualDisplay)
            closeSafely(imageReader)
            stopSafely(projection)
            onScreenshotCaptureFailed(e)
        }
    }

    @SuppressLint("WrongConstant")
    private fun createImageReader(
        projection: MediaProjection,
        spec: ScreenshotSpec,
        callbackHandler: Handler
    ): ImageReader {
        return ImageReader.newInstance(spec.width, spec.height, PixelFormat.RGBA_8888, 2).also {
            it.setOnImageAvailableListener(
                ImageAvailableListener(
                    projection,
                    spec.width,
                    spec.height
                ), callbackHandler
            )
        }
    }

    private fun createVirtualDisplay(
        projection: MediaProjection,
        surface: Surface,
        spec: ScreenshotSpec,
        callbackHandler: Handler
    ): VirtualDisplay {
        return projection.createVirtualDisplay(
            CAPTURE_THREAD_NAME,
            spec.width,
            spec.height,
            spec.densityDpi,
            VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            null,
            callbackHandler
        )
    }

    private fun onScreenshotCaptured(bitmap: Bitmap) {
        mainThreadHandler.post {
            pendingResult?.submit(Result.Success(bitmap))
            pendingResult = null
            stopCaptureThread()
        }
    }

    private fun onScreenshotCaptureFailed(cause: Exception) {
        mainThreadHandler.post {
            pendingResult?.submit(Result.Error(cause.message))
            pendingResult = null
            stopCaptureThread()
        }
    }

    private fun startCaptureThread(): HandlerThread {
        checkOnMainThread()
        var thread = captureThread
        if (thread == null) {
            thread = HandlerThread(CAPTURE_THREAD_NAME)
            thread.start()
            captureThread = thread
        }
        return thread
    }

    private fun stopCaptureThread() {
        captureThread?.let {
            interruptSafely(it)
            captureThread = null
        }
    }

    private fun getMediaProjection(accessData: ProjectionAccessData): MediaProjection? {
        return accessData.data?.let { data ->
            projectionManager.getMediaProjection(
                accessData.resultCode,
                data
            )
        }
    }

    private fun Activity.getMediaProjectionManager(): MediaProjectionManager = requireNotNull(
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
    )

    private inner class ImageAvailableListener constructor(
        private val projection: MediaProjection,
        private val width: Int,
        private val height: Int
    ) : ImageReader.OnImageAvailableListener {
        private var processed = false

        override fun onImageAvailable(reader: ImageReader) {
            if (processed) return
            processed = true
            var image: Image? = null
            var bitmap: Bitmap? = null
            try {
                image = reader.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    bitmap = Bitmap.createBitmap(
                        width + rowPadding / pixelStride,
                        height,
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap?.copyPixelsFromBuffer(buffer)
                    onScreenshotCaptured(bitmap)
                } else {
                    val exception = Exception("Failed to create MediaProjection object")
                    onScreenshotCaptureFailed(exception)
                }
            } catch (e: Exception) {
                onScreenshotCaptureFailed(e)
                bitmap?.recycle()
            } finally {
                image?.close()
                stopSafely(projection)
            }
        }
    }

    private class ReleaseOnStopCallback constructor(
        private val projection: MediaProjection,
        private val imageReader: ImageReader,
        private val virtualDisplay: VirtualDisplay
    ) : MediaProjection.Callback() {

        override fun onStop() {
            releaseSafely(virtualDisplay)
            closeSafely(imageReader)
            projection.unregisterCallback(this)
        }
    }

    private class ProjectionAccessData(
        val resultCode: Int,
        val data: Intent?
    )

    companion object {
        private const val CAPTURE_THREAD_NAME = "capture"

        private var LAST_ACCESS_DATA: ProjectionAccessData? = null
    }
}
