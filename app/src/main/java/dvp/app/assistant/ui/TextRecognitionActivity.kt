package dvp.app.assistant.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dvp.app.assistant.R
import dvp.app.assistant.services.OverlayService
import dvp.app.assistant.utils.Capture
import java.io.IOException


class TextRecognitionActivity : AppCompatActivity() {

    private lateinit var imgView: ImageView
    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var textView: TextView
    private lateinit var btProcess: Button
    private lateinit var overlayView: OverlayView

    private lateinit var inputImage: InputImage

    private val listFile by lazy {
        assets.list("demo")
    }

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
        TextRecognition.getClient(
            JapaneseTextRecognizerOptions.Builder().build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text_recognition_activity)
        findViews()
        setUpViews()
        Capture.init(this)
        ContextCompat.startForegroundService(
            this,
            Intent(this, OverlayService::class.java)
        )
    }

    private fun findViews() {
        imgView = findViewById(R.id.imageView)
        spinner = findViewById(R.id.spinner)
        radioGroup = findViewById(R.id.langGroup)
        textView = findViewById(R.id.textView)
        btProcess = findViewById(R.id.btProcess)
        overlayView = findViewById(R.id.overlayView)
    }

    private fun setUpViews() {

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listFile!!.asList().toTypedArray()
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    val bitmap = getBitmapFromAsset(filePath = "demo/${listFile!![position]}")
                    inputImage = InputImage.fromBitmap(bitmap, 0)
                    imgView.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    Log.d("TEST", "load image error ${e.localizedMessage}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getTextRecognizer(): TextRecognizer {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.rChinese -> chineseRecognizer
            R.id.rJapanese -> japaneseRecognizer
            else -> latinRecognizer
        }
    }

    private fun getBitmapFromAsset(filePath: String?): Bitmap {
        return try {
            val `is` = this.assets.open(filePath!!)
            BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            throw Exception("image not found")
        }
    }

    data class Ratio(val w: Float, val h: Float)

    private fun calRatio(r: (Ratio) -> Unit) {
        overlayView.post {
            val oW = inputImage.width.toFloat()
            val oH = inputImage.height.toFloat()
            val w = overlayView.width.toFloat()
            val h = overlayView.height.toFloat()
            r.invoke(Ratio(oW / w, oH / h))
        }
    }

    fun processImage(view: View) {
        ContextCompat.startForegroundService(
            this,
            Intent(this, OverlayService::class.java)
        )
//        textView.text = ""
//
//        getTextRecognizer().process(inputImage)
//            .addOnSuccessListener { visionText ->
//                calRatio { r ->
//                    val regex = Regex("^[a-zA-Z0-9!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]*\$")
//
//                    val elements = visionText.textBlocks
//                        .flatMap {
//                            it.lines.flatMap { line ->
//                                line.elements
//                            }
//                        }
//                        .filterNot {
//                            val text = it.text
//                            val matched = regex.matches(text)
//                            Log.d("TEST", "Text=$text, isMatched=$matched")
//                            matched
//                        }
//
//                    elements.mapNotNull {
//                        it.boundingBox?.run {
//                            val rect = Rect(
//                                (this.left / r.w).toInt(),
//                                (this.top / r.h).toInt(),
//                                (this.right / r.w).toInt(),
//                                (this.bottom / r.h).toInt()
//                            )
//
//                            return@run rect to it.text
//                        }
//                    }.also {
//                        overlayView.drawListRect(it)
//                    }
//
//                    elements.forEach {
//                        textView.append(it.text)
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.d("TEST", "Recognition failed $e")
//            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Capture.screenShot!!.onActivityResult(requestCode, resultCode, data)
    }
}