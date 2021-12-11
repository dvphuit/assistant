package dvp.app.assistant.services.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.toRectF
import dvp.app.assistant.R
import dvp.app.assistant.base.ext.getWindowManager
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.utils.dp
import dvp.app.assistant.utils.screenSizeCompat


class DetectionView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val currentTextView: BubbleTextView by lazy {
        BubbleTextView(context, this)
    }
    private var textBlocks = emptyList<TextBlock>()

    private val roundedRadius = 5.dp
    private val padding = 4.dp
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#22000000")
        style = Paint.Style.FILL
    }

    private val wm by lazy { context.getWindowManager() }

    private val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                this@DetectionView.run {
                    removeAllViews()
                    wm.removeView(this)
                }
                return super.onDoubleTap(e)
            }

            override fun onDown(e: MotionEvent): Boolean {
                showPopup(e)
                return super.onDown(e)
            }
        })
    }

    fun setTextBlocks(textBlocks: List<TextBlock>) {
        this.textBlocks = textBlocks
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        textBlocks.forEach { tb ->
            canvas.drawRoundRect(
                tb.rect.padding(),
                roundedRadius,
                roundedRadius,
                bgPaint
            )
        }
    }

    private fun showPopup(event: MotionEvent) {
        val found = textBlocks.find { it.rect.contains(event.x.toInt(), event.y.toInt()) }
        if (currentTextView.text == found?.trans) {
            return
        }
        if (found != null) {
            currentTextView.show(found.trans ?: "Empty", found.rect, found.lines)
        } else {
            currentTextView.hide()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun Rect.padding(dp: Float = padding) = this.toRectF().apply {
        this.top -= dp
        this.left -= dp
        this.bottom += dp
        this.right += dp
    }
}