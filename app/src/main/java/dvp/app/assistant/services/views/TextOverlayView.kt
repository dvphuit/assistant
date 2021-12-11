package dvp.app.assistant.services.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import dvp.app.assistant.base.ext.getWindowManager
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.services.views.fittext.FitTextImpl
import dvp.app.assistant.services.views.popup.PopupTextImpl


class TextOverlayView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val wm by lazy { context.getWindowManager() }

    private val popupStyle = true

    private val textDraw: IDraw by lazy {
        if (popupStyle) {
            PopupTextImpl(this)
        } else {
            FitTextImpl(this)
        }
    }

    private val gestureDetector by lazy {
        GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                wm.removeView(this@TextOverlayView)
                textDraw.close()
                return super.onDoubleTap(e)
            }

            override fun onDown(e: MotionEvent): Boolean {
                textDraw.onTouch(e)
                return super.onDown(e)
            }
        })
    }

    private var textBlocks = emptyList<TextBlock>()

    fun setTextBlocks(textBlocks: List<TextBlock>) {
        this.textBlocks = textBlocks
        this.textDraw.setTextBlocks(textBlocks)
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        textBlocks.forEach { tb ->
            textDraw.draw(canvas, tb.trans ?: "N/a", tb.rect)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
