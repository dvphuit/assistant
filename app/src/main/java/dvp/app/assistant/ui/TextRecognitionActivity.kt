package dvp.app.assistant.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dvp.app.assistant.R
import dvp.app.assistant.services.ocr.TextRecognizer
import dvp.app.assistant.services.OverlayService
import dvp.app.assistant.services.Capture
import dvp.app.assistant.services.ocr.RecognizerLanguages
import java.io.IOException


class TextRecognitionActivity : AppCompatActivity() {

    private lateinit var imgView: ImageView
    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var textView: TextView
    private lateinit var btProcess: Button

    private val listFile by lazy { assets.list("demo") }

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
                    imgView.setImageBitmap(bitmap)

                    val lang = when (radioGroup.checkedRadioButtonId) {
                        R.id.rChinese -> RecognizerLanguages.CHINESE
                        R.id.rJapanese -> RecognizerLanguages.JAPANESE
                        else -> RecognizerLanguages.LATIN
                    }
                    TextRecognizer.setRecognizerLanguage(lang)

                } catch (e: Exception) {
                    Log.d("TEST", "load image error ${e.localizedMessage}")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getBitmapFromAsset(filePath: String?): Bitmap {
        return try {
            this.assets.open(filePath!!).run {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) {
            throw Exception("image not found")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Capture.onActivityResult(requestCode, resultCode, data)
    }

    fun processImage(view: View) {

    }


}