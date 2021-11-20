package dvp.app.assistant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint.FontMetricsInt
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import dvp.app.assistant.ui.FontSizeUtils.SizeTester
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import dvp.app.assistant.R

@SuppressLint("AppCompatCustomView", "SetTextI18n")
class AutoTextSize @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextView(context, attrs, defStyle) {

    private var mText =
        "metaphor is the unifying  metaphos thefying  metaphor is the unifying  metaphor is the unifying  mehor is the unifying   metaphor is the unifying metaphor is the unifying metaphor is the unietaphor is the unifying metaphor is thmetapho is the unifying metaphor is the unifying metaphor is the unifying metaphor is the unifying metaphor is the unifying\\n\\n\\A material metaphor is the unifying A material metaphor is the unifying  metaphor is the unifying A material metaphor is the unifying"

    private val _availableSpaceRect = RectF()
    private var mFontSizeUtils: FontSizeUtils = FontSizeUtils().apply {
        sizeTester = SizeTester(DEFAULT_MIN_TEXT_SIZE, DEFAULT_MAX_TEXT_SIZE)
    }

    private val textPaint = TextPaint().apply {
        this.isAntiAlias = true
        this.textAlign = Paint.Align.CENTER
        this.color = Color.BLACK
        this.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        this.isSubpixelText = true
    }

    private fun adjustTextSize() {
        val heightLimit = measuredHeight - 0 - 0
        val widthLimit = measuredWidth - 0 - 0
        if (widthLimit <= 0) {
            return
        }
        _availableSpaceRect.right = widthLimit.toFloat()
        _availableSpaceRect.bottom = heightLimit.toFloat()
        superSetTextSize()
    }

    private fun superSetTextSize() {
        val size = efficientTextSizeSearch(_availableSpaceRect)
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        var newText = mText.toString().trim { it <= ' ' }
        if (size - 1 <= DEFAULT_MIN_TEXT_SIZE) {
            newText = StringUtils.ellipsizeText(newText, _availableSpaceRect.width(), textPaint)
        }
        val fixString = ""
        super.setText(newText + fixString)
    }

    private fun efficientTextSizeSearch(availableSpace: RectF): Float {
        val text = mText.toString().trim { it <= ' ' }
        val isSingleLine = StringUtils.isSingleLine(text)
        val newSize =
            mFontSizeUtils!!.getFontSize(text, isSingleLine, availableSpace, textPaint).toFloat()
        println(newSize)
        return newSize
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        if (width != oldwidth || height != oldheight) adjustTextSize()
    }


    private var fontMetricsInt: FontMetricsInt? = null

    override fun onDraw(canvas: Canvas) {
//        if (fontMetricsInt == null) {
//            fontMetricsInt = FontMetricsInt()
//            paint.getFontMetricsInt(fontMetricsInt)
//        }
//        canvas.translate(0f, (fontMetricsInt!!.top - fontMetricsInt!!.ascent).toFloat())
        super.onDraw(canvas)
    }

    companion object {
        // Minimum size of the text in pixels
        private const val DEFAULT_MIN_TEXT_SIZE = 8.0f //sp
        private const val DEFAULT_MAX_TEXT_SIZE = 80.0f //sp
    }

}