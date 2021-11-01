package dvp.app.assistant.screenshot.internal

import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import dvp.app.assistant.BuildConfig

internal object Utils {

    private const val LOG_TAG = "screenshot"

    private val LOG_ENABLED = BuildConfig.DEBUG

    fun checkOnMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw IllegalAccessException("The method can be called only on the main thread")
        }
    }

    fun closeSafely(image: Image?) = doSafely {
        image?.close()
    }

    fun closeSafely(reader: ImageReader?) = doSafely {
        reader?.close()
    }

    fun stopSafely(projection: MediaProjection?) = doSafely {
        projection?.stop()
    }

    fun releaseSafely(display: VirtualDisplay?) = doSafely {
        display?.release()
    }

    fun interruptSafely(thread: HandlerThread) = doSafely {
        thread.quitSafely()
        thread.interrupt()
    }

    private inline fun doSafely(action: () -> Unit) = try {
        action()
    } catch (e: Exception) {
        logE(e)
    }

    fun logE(throwable: Throwable) {
        if (LOG_ENABLED) {
            Log.e(LOG_TAG, throwable.message, throwable)
        }
    }

    fun logE(message: String) {
        if (LOG_ENABLED) {
            Log.e(LOG_TAG, message)
        }
    }
}