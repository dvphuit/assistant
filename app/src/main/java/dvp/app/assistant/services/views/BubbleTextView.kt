package dvp.app.assistant.services.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.transition.TransitionManager
import dvp.app.assistant.R
import dvp.app.assistant.services.views.transition.BubbleTextTransition
import dvp.app.assistant.utils.dp
import dvp.app.assistant.utils.screenSizeCompat
import java.lang.Float.min


@SuppressLint("AppCompatCustomView")
class BubbleTextView : TextView {
    private lateinit var parent: ViewGroup

    constructor(context: Context, viewParent: ViewGroup) : super(context) {
        this.parent = viewParent
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val popupShowAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.popup_show)
    }

    private val popupHideAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.popup_hide)
    }

    private val screenSize by lazy {
        context.screenSizeCompat()
    }

    private val bgPaint = Paint().apply {
        this.isAntiAlias = true
        this.strokeWidth = 3f
        this.color = Color.RED
        this.style = Paint.Style.FILL
    }

    private val bgRoundRadius = 5.dp
    private val arrowSize = 10.dp
    private val padding = 4.dp.toInt()
    private var arrowX: Float = 0f
    private var topArrow: Boolean = false

    private var isShowing = false

    private fun init() {
        this.textSize = 14f
        this.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        this.setPadding(padding, 0, padding, 0)
    }

//    override fun onDraw(canvas: Canvas) {
//        bubbleBackground(canvas)
//        super.onDraw(canvas)
//    }

    private fun bubbleBackground(size: Size): Path {
        val rectF = RectF(0f, 0f, size.width.toFloat(), size.height.toFloat())
        val path = roundedRectPath(
            rectF,
            bgRoundRadius,
            bgRoundRadius,
            bgRoundRadius,
            bgRoundRadius,
            topArrow = topArrow,
            arrowX = arrowX,
            arrowSize = arrowSize
        )

//        val bitmap = Bitmap.createBitmap(
//            size.width,
//            size.height,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmap)
//        canvas.drawPath(path, bgPaint)
//        background = BitmapDrawable(resources, bitmap)

        return path
    }

    private var startRect: Rect? = null
    private var endRect: Rect? = null
    private var backgroundData: Path? = null

    fun show(text: String, rect: Rect, lines: Int) {
        this.hide()
        this.startRect = rect
        this.maxLines = lines
        this.text = text
        this.maxWidth = min((rect.width() * 2f), screenSize.width - 8.dp).toInt()

        val size = getViewSize(this@BubbleTextView)
        val x = calcX(rect, size.width)
        val y = calcY(rect, size.height)
        if (topArrow) {
            updatePadding(top = padding + arrowSize.toInt(), bottom = padding)
        } else {
            updatePadding(top = padding, bottom = padding + arrowSize.toInt())
        }

        this.backgroundData = bubbleBackground(size)
        this.parent.addView(this@BubbleTextView)
        val transition = BubbleTextTransition().apply {
            setStartBound(rect)
            endRect = Rect(
                x.toInt(),
                y.toInt(),
                (x + size.width).toInt(),
                (y + size.height).toInt()
            )
            setEndBound(endRect!!)
            setBackground(Color.LTGRAY, Color.RED, backgroundData!!)
            addTarget(this@BubbleTextView)
        }

        TransitionManager.beginDelayedTransition(parent, transition)
        this.isShowing = true
    }

    fun hide() {
        if (!this.isShowing) return

        this.text = ""

        val transition = BubbleTextTransition().apply {
            setStartBound(endRect!!)
            setEndBound(startRect!!)
            setBackground(Color.RED, Color.LTGRAY, backgroundData!!)
            addTarget(this@BubbleTextView)
        }
        TransitionManager.beginDelayedTransition(parent, transition)
        this.isShowing = false
    }

    private fun calcX(rect: Rect, viewWidth: Int): Float {
        arrowX = viewWidth / 2f
        var x: Float
        if (viewWidth < rect.width()) {
            return rect.left + (rect.width() - viewWidth) / 2f
        } else {
            x = rect.left - (viewWidth - rect.width()) / 2f
            if (x < 0) {
                this.arrowX = rect.centerX().toFloat()
                return 4.dp
            }
            val outRight = x + viewWidth - screenSize.width
            if (outRight > 0) {
                arrowX -= outRight
                x -= outRight
            }
            return x
        }
    }

    private fun calcY(rect: Rect, viewHeight: Int): Float {
        topArrow = false
        var y = rect.top.toFloat() - viewHeight
        if (y < 0) {
            y = rect.bottom.toFloat()
            topArrow = true
            if (y + viewHeight > screenSize.height) {
                topArrow = false
                y = 0f
            }
        }
        return y
    }

    private fun getViewSize(view: View): Size {
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenSize.width, MeasureSpec.AT_MOST)
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return Size(
            view.measuredWidth,
            view.measuredHeight
        )
    }

    private fun roundedRectPath(
        rect: RectF,
        tlRadius: Float,
        trRadius: Float,
        brRadius: Float,
        blRadius: Float,
        topArrow: Boolean = false,
        arrowX: Float = 0f,
        arrowSize: Float = 10.dp
    ): Path {
        var pathStr = ""
        if (topArrow) {
            rect.set(rect.left, rect.top + arrowSize, rect.right, rect.bottom)
        } else {
            rect.set(rect.left, rect.top, rect.right, rect.bottom - arrowSize)
        }

        return Path().apply {
            moveTo(rect.left + tlRadius, rect.top)//init
            pathStr += "M${rect.left + tlRadius} ${rect.top}"

            if (arrowX > 0 && topArrow) {
                lineTo(arrowX - arrowSize * .75f, rect.top)
                pathStr += " L${arrowX - arrowSize * .75f} ${rect.top}"
                lineTo(arrowX, rect.top - arrowSize)
                pathStr += " L${arrowX} ${rect.top - arrowSize}"
                lineTo(arrowX + arrowSize * .75f, rect.top)
                pathStr += " L${arrowX + arrowSize * .75f} ${rect.top}"
            }
            lineTo(rect.right - trRadius, rect.top) //left to right
            pathStr += " L${rect.right - trRadius} ${rect.top}"

            quadTo(rect.right, rect.top, rect.right, rect.top + trRadius) //rounded top-right
            pathStr += " Q${rect.right} ${rect.top} ${rect.right} ${rect.top + trRadius}"

            lineTo(rect.right, rect.bottom - brRadius) //top right to bottom
            pathStr += " L${rect.right} ${rect.bottom - brRadius}"
            quadTo(
                rect.right,
                rect.bottom,
                rect.right - brRadius,
                rect.bottom
            ) //rounded bottom right
            pathStr += " Q${rect.right} ${rect.bottom} ${rect.right - brRadius} ${rect.bottom}"

            if (arrowX > 0 && !topArrow) {
                lineTo(arrowX - arrowSize * .75f, rect.bottom)
                pathStr += " L${arrowX - arrowSize * .75f} ${rect.bottom}"
                lineTo(arrowX, rect.bottom + arrowSize)
                pathStr += " L${arrowX} ${rect.bottom + arrowSize}"
                lineTo(arrowX + arrowSize * .75f, rect.bottom)
                pathStr += " L${arrowX + arrowSize * .75f} ${rect.bottom}"
            }

            lineTo(rect.left + blRadius, rect.bottom) //right to left
            pathStr += " L${rect.left + blRadius} ${rect.bottom}"

            quadTo(rect.left, rect.bottom, rect.left, rect.bottom - blRadius) //rounded bottom left
            pathStr += " Q${rect.left} ${rect.bottom} ${rect.left} ${rect.bottom - blRadius}"

            lineTo(rect.left, rect.top + tlRadius) //bottom to top
            pathStr += " L${rect.left} ${rect.top + tlRadius}"

            quadTo(rect.left, rect.top, rect.left + tlRadius, rect.top) //rounded top left
            pathStr += " Q${rect.left} ${rect.top} ${rect.left + tlRadius} ${rect.top}"

            close() //end
            pathStr += " Z"

            Log.d("TEST", "path $pathStr")
        }
    }

    private fun updatePadding(
        top: Int = paddingTop,
        left: Int = paddingStart,
        bottom: Int = paddingBottom,
        right: Int = paddingEnd
    ) {
        setPadding(left, top, right, bottom)
    }
}