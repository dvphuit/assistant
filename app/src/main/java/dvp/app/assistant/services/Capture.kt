package dvp.app.assistant.services

import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.WindowManager
import dvp.app.assistant.screenshot.Result
import dvp.app.assistant.screenshot.ScreenShot
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.services.ocr.TextRecognizer
import dvp.app.assistant.services.translator.api.Apis
import dvp.app.assistant.services.translator.model.Translate
import dvp.app.assistant.services.views.DetectionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

object Capture {

    private var overlay: DetectionView? = null
    private var screenShot: ScreenShot? = null
    private lateinit var windowManager: WindowManager
    private val textOverlayParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
    }

    fun init(activity: Activity) {
        screenShot = ScreenShot(activity)
        windowManager = activity.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    fun setOverlay(overlayView: DetectionView) {
        overlay = overlayView
    }

    fun screenShoot() {
        screenShot!!
            .makeScreenshot()
            .observe {
                when (it) {
                    is Result.Success -> TextRecognizer.process(it.bitmap) { textBlocks ->
                        Log.d("TEST", "TextBlocks $textBlocks")
                        translate(textBlocks) { translated ->
                            overlay?.apply {
                                windowManager.addView(this, textOverlayParams)
                                setTextBlocks(translated)
                            }
                        }

                    }
                    is Result.Error -> {
                        Log.e("TEST", "error $it")
                    }
                }
            }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        screenShot?.onActivityResult(requestCode, resultCode, data)
    }

    private fun translate(textBlocks: List<TextBlock>, sentences: (List<TextBlock>) -> Unit) {
        val text = textBlocks.joinToString("#") { it.src }

        val translated = text
            .split("#")
            .mapIndexed { index, s ->
                textBlocks[index].apply { this.trans = s }
            }
        sentences.invoke(translated)

//        GlobalScope.launch(Dispatchers.IO) {
//            Apis.translate()
//                .listSentence(query = text)
//                .enqueue(object : retrofit2.Callback<Translate> {
//                    override fun onResponse(call: Call<Translate>, response: Response<Translate>) {
//                        val translated = response.body()!!.sentences
//                            .joinToString { it.trans.replace("\n","") }
//                            .split("#")
//                            .mapIndexed { index, s ->
//                                textBlocks[index].apply { this.trans = s }
//                            }
//                        sentences.invoke(translated)
//                    }
//
//                    override fun onFailure(call: Call<Translate>, t: Throwable) {
//                        Log.d("TEST", t.message ?: "empty")
//                    }
//                })
//        }
    }

}