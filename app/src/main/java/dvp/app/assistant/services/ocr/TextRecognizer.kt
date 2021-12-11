package dvp.app.assistant.services.ocr

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dvp.app.assistant.services.views.TextOverlayView

object TextRecognizer {

    private val latinRecognizer by lazy {
        TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        )
    }

    private val chineseRecognizer by lazy {
        TextRecognition.getClient(
            ChineseTextRecognizerOptions.Builder().build()
        )
    }

    private val japaneseRecognizer by lazy {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }

    private var textRecognizer: TextRecognizer = japaneseRecognizer

    private var inputImage: InputImage? = null
    private var overlayView: TextOverlayView? = null


    fun setRecognizerLanguage(recognizer: RecognizerLanguages) {
        textRecognizer = when (recognizer) {
            RecognizerLanguages.LATIN -> latinRecognizer
            RecognizerLanguages.CHINESE -> chineseRecognizer
            RecognizerLanguages.JAPANESE -> japaneseRecognizer
        }
    }

    fun process(bitmap: Bitmap, textBlocks: (List<TextBlock>) -> Unit) {
        inputImage = InputImage.fromBitmap(bitmap, 0).also { im ->
            textRecognizer.process(im)
                .addOnSuccessListener { visionText ->
                    val ratio = calculateRatio(im, overlayView)
                    val elements = filterText(visionText)
                    textBlocks.invoke(getRealTextBlocks(elements, ratio))
                }
                .addOnFailureListener { e ->
                    Log.d("TEST", "Recognition failed $e")
                    inputImage = null
                }
        }
    }

    private fun calculateRatio(src: InputImage, overlay: TextOverlayView?): Pair<Float, Float> {
        if (overlay == null) return 1f to 1f

        val oW = src.width.toFloat()
        val oH = src.height.toFloat()
        val w = overlay.width.toFloat()
        val h = overlay.height.toFloat()
        return oW / w to oH / h
    }

    private fun getRealTextBlocks(
        elements: List<Text.TextBlock>,
        ratio: Pair<Float, Float>
    ): List<TextBlock> {
        return elements.mapNotNull {
            it.boundingBox?.run {
                val rect = Rect(
                    (this.left / ratio.first).toInt(),
                    (this.top / ratio.second).toInt(),
                    (this.right / ratio.first).toInt(),
                    (this.bottom / ratio.second).toInt()
                )
//                TextBlock(rect, TextFilter.removeBr(it.text))
                TextBlock(rect, it.text, lines = it.lines.size)
            }
        }
    }

    private fun filterText(visionText: Text): List<Text.TextBlock> {
        return visionText.textBlocks.filter { TextFilter.containsJp(it.text) }
//            .flatMap {
//                it.lines.flatMap { line ->
//                    line.elements
//                }
//            }
//            .filterNot { latin.matches(it.text) }
    }
}