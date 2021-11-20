package dvp.app.assistant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.graphics.toRect


@SuppressLint("AppCompatCustomView")
class Custom : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mText =
        "Are you looking for the Best Courses to Learn Deep Learning? If yes, then you are in the right place. In this article, you will find the 12 Best Courses to Learn Deep Learning. So give few minutes and find out the best deep learning course for you."

    private val listText = listOf(
        "Are\nyou\nlooking",
        "Are you looking for the\nBest Courses to\nLearn Deep Learning",
        "Are you looking for the Best Courses to Learn Deep Learning? If yes, then you are in the right place. In this article"
    )

    private val listRect = listOf(
        Rect(0, 0, 400, 300),
        Rect(420, 110, 800, 400),
        Rect(900, 50, 1300, 400),
    )


    private val textPaint = TextPaint().apply {
        color = Color.RED
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textSize = 100f
    }
    private val paint = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in listRect.indices) {
            val rect = listRect[i]
            val text = listText[i]

            canvas?.drawRect(rect, paint)
            val size = applyFitText(textPaint, text, rect)
            textPaint.textSize = size
            canvas?.drawText(textPaint, text, rect)
        }

    }

    private fun Canvas.drawText(textPaint: TextPaint, text: String, rect: Rect) {
        val sl = StaticLayout.Builder
            .obtain(text, 0, text.length, textPaint, rect.width())
            .build()

        save()
        translate(rect.left.toFloat(), rect.top.toFloat())
        sl.draw(this)
        restore()
    }

    private fun applyFitText(textPaint: TextPaint, text: String, rect: Rect): Float{
        val paint = TextPaint(textPaint)
        val lineCount = getLineCount(paint, rect.width(), text)
        var height = getHeight(paint, lineCount)
        while (height > rect.height()) {
            paint.textSize -= 5
            height = getHeight(paint, getLineCount(paint, rect.width(), text))
        }
        return paint.textSize
    }

    private fun getHeight(textPaint: TextPaint, lineCount: Int): Float {
        val paint = textPaint
        val lineHeight = paint.fontMetrics.run { descent - ascent }
        return lineCount * lineHeight + if (lineCount > 0) lineCount - 1 else 0
    }

    private fun getLineCount(textPaint: TextPaint, maxWidth: Int, text: CharSequence): Int {
        val paint = textPaint
        var lineCount = 0
        var index = 0
        while (index < text.length) {
            index += paint.breakText(text, index, text.length, true, maxWidth.toFloat(), null)
            lineCount++
        }
        return lineCount
    }
}