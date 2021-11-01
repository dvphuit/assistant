package dvp.app.assistant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

class OverlayView : View {

    private val boxes = mutableListOf<Pair<Rect, String>>()
    private val paint = Paint().apply {
        color = Color.GREEN
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val textPaint = TextPaint().apply {
        color = Color.RED
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textSize = 30f
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun drawListRect(rect: List<Pair<Rect, String>>) {
        boxes.clear()
        boxes.addAll(rect)
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        boxes.forEach {
            canvas?.drawRect(it.first, paint)

            textPaint.textSize = getFitTextSize(textPaint, it.first.width(), it.second)
            canvas?.drawText(it.second, it.first.left.toFloat(), it.first.top.toFloat(), textPaint)
        }
    }

    private fun getFitTextSize(paint: TextPaint, width: Int, text: String): Float {
        val nowWidth = paint.measureText(text)
        return width / nowWidth * paint.textSize
    }
}