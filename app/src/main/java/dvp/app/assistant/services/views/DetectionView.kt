package dvp.app.assistant.services.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toRectF
import androidx.core.view.children
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import dvp.app.assistant.R
import dvp.app.assistant.base.ext.getWindowManager
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.services.views.transition.BubbleTextTransition
import dvp.app.assistant.utils.dp
import kotlin.math.max


class DetectionView : ViewGroup {
    constructor(context: Context) : super(context) {
        setBackgroundColor(Color.parseColor("#33000000"))
    }

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
                    visibleTexts.clear()
                }
                return super.onDoubleTap(e)
            }

            override fun onDown(e: MotionEvent): Boolean {
                showPopup(e)
                return super.onDown(e)
            }
        })
    }

    private val detectViewIds = mutableListOf<Int>()

    fun setTextBlocks(textBlocks: List<TextBlock>) {
        this.textBlocks = textBlocks
//        invalidate()

        textBlocks.forEach {
            val detectViews = DetectView(context).apply {
                id = ("detect${it.src}").hashCode()
                tag = "detect"
                detectViewIds.add(id)
                val paddedRect = it.rect.padding()
                this.layoutParams = DetectionView.LayoutParams(
                    paddedRect.width().toInt(),
                    paddedRect.height().toInt(),
                    paddedRect.left.toInt(),
                    paddedRect.top.toInt()
                )
            }
            addView(detectViews)
        }
    }


    private val visibleTexts = mutableListOf<Int>()

    private fun showPopup(event: MotionEvent) {
        val found = textBlocks.find { it.rect.contains(event.x.toInt(), event.y.toInt()) }

        if (visibleTexts.contains(found?.trans.hashCode())) {
            return
        }
        if (found != null) {
            val bubbleView = BubbleTextView(context, this).apply {
                setBackgroundColor(Color.RED)
                this.layoutParams = LayoutParams(found.rect)
            }
//            this.addView(bubbleView)
//            bubbleView.post {
//                bubbleView.build(found.src, found.rect.toRectF(), found.lines) { b, c ->
//                    val transition = BubbleTextTransition().apply {
//                        setStartBound(found.rect.toRectF())
//                        setEndBound(b.toRectF())
////                    setBackground(Color.RED, Color.LTGRAY, backgroundData!!)
//                        addTarget(bubbleView)
//                    }
//
//                    bubbleView.layoutParams = LayoutParams(b.width(), b.height(), b.left, b.top)
//                    TransitionManager.beginDelayedTransition(
//                        this,
//                        ChangeBounds().addTarget(bubbleView)
//                    )
//                    bubbleView.invalidate()
//                };
//            }

            bubbleView.show(found.src, found.rect, found.lines)
        } else {
            children.iterator().forEach {
                if (it.tag != "detect") {
                    removeView(it)
                    visibleTexts.remove(it.id)
                }
            }
        }
    }


    private fun Rect.padding(dp: Float = padding) = this.toRectF().apply {
        this.top -= dp
        this.left -= dp
        this.bottom += dp
        this.right += dp
    }

    private fun getViewSize(view: View): Size {
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(this.width, MeasureSpec.AT_MOST)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        this.measure(widthMeasureSpec, heightMeasureSpec)
        return Size(view.measuredWidth, view.measuredHeight)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        var maxHeight = 0
        var maxWidth = 0
        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        // Find rightmost and bottom-most child
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                var childRight: Int
                var childBottom: Int
                val lp = child.layoutParams as LayoutParams
                childRight = lp.x + child.measuredWidth
                childBottom = lp.y + child.measuredHeight
                maxWidth = max(maxWidth, childRight)
                maxHeight = max(maxHeight, childBottom)
            }
        }
        // Account for padding too
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        // Check against minimum height and width
        maxHeight = max(maxHeight, suggestedMinimumHeight)
        maxWidth = max(maxWidth, suggestedMinimumWidth)
        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
            resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
        )
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val lp = child.layoutParams as LayoutParams
                val childLeft: Int = paddingLeft + lp.x
                val childTop: Int = paddingTop + lp.y
                child.layout(childLeft, childTop,childLeft + child.measuredWidth,childTop + child.measuredHeight)
            }
        }
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams? {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0,
            0
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams? {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        return super.generateLayoutParams(p)
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return super.checkLayoutParams(p)
    }

    class LayoutParams : ViewGroup.LayoutParams {
        var x = 0
        var y = 0

        constructor(width: Int, height: Int, x: Int = 0, y: Int = 0) : super(width, height) {
            this.x = x
            this.y = y
        }

        constructor(rect: Rect) : super(rect.width(), rect.height()) {
            this.x = rect.left
            this.y = rect.top
        }

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.DetectionView_Layout)
            x = a.getDimensionPixelOffset(R.styleable.DetectionView_Layout_x, 0)
            y = a.getDimensionPixelOffset(R.styleable.DetectionView_Layout_y, 0)
            a.recycle()
        }

        constructor(source: ViewGroup.LayoutParams?) : super(source) {}
    }

}