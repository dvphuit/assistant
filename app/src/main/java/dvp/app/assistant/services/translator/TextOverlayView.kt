package dvp.app.assistant.services.translator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import dvp.app.assistant.services.ocr.TextBlock


class TextOverlayView : View {

    private val wm by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val gestureDetector by lazy {
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                wm.removeView(this@TextOverlayView)
                return super.onDoubleTap(e)
            }
        })
    }

    private var textBlocks = emptyList<TextBlock>()

    private val bgPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }
    private val textPaint = TextPaint().apply {
        color = Color.RED
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textSize = 100f
    }

    private val textSizes = mutableListOf<Float>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun drawTextBlocks(textBlocks: List<TextBlock>) {
        this.textBlocks = textBlocks
        textBlocks.forEach {
            if (!it.trans.isNullOrEmpty()) {
                val size = applyFitText(textPaint, it.trans!!, it.rect)
                textSizes.add(size)
            } else {
                textSizes.add(10f)
            }
        }

        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textBlocks.forEachIndexed { index, tb ->
            canvas?.drawRect(tb.rect, bgPaint)
            val mPaint = textPaint
            mPaint.textSize = textSizes[index]

            canvas?.drawText(mPaint, tb.trans ?: "Error", tb.rect)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
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

    private fun applyFitText(textPaint: TextPaint, text: String, rect: Rect): Float {
        val paint = textPaint
        val lineCount = getLineCount(rect.width(), text)
        var height = getHeight(lineCount)
        while (height > rect.height()) {
            paint.textSize -= 2
            height = getHeight(getLineCount(rect.width(), text))
        }
        return paint.textSize
    }

    private fun getHeight(lineCount: Int): Float {
        val paint = textPaint
        val lineHeight = paint.fontMetrics.run { descent - ascent }
        return lineCount * lineHeight + if (lineCount > 0) lineCount - 1 else 0
    }

    private fun getLineCount(maxWidth: Int, text: CharSequence): Int {
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