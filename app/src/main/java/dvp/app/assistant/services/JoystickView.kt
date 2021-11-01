package dvp.app.assistant.services

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


enum class Direction {
    CENTER,
    UP,
    LEFT,
    RIGHT,
    BOTTOM,
}

enum class State {
    IDLE,
    SWIPE,
    HOLDING
}

class JoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var state = State.IDLE

    private val timer = object : CountDownTimer(1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            Log.d("TEST", "Timer finished")
            state = State.HOLDING
        }
    }

    private var stickPoint = PointF(0f, 0f)
    private var cp = 0f
    private var maxDistance = 0f
    private var rStick = 0f
    private var rBase = 0f

    private val stickPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    private val basePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    private var listener: ((Direction) -> Unit)? = null

    fun setDirectionListener(listener: (Direction) -> Unit) {
        this.listener = listener
    }

    private val resetAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100
        interpolator = BounceInterpolator()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.stickPoint = PointF(w.toFloat() / 2, h.toFloat() / 2)
        this.cp = min(w, h) / 2f
        this.rBase = w / 3f
        this.rStick = this.rBase * .9f
        this.maxDistance = this.rBase - this.rStick + 10
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(cp, cp, rBase, basePaint)
        canvas.drawCircle(stickPoint.x, stickPoint.y, rStick, stickPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == State.HOLDING) {
                    Log.d("TEST", "Button Dragging ${event.x}/${event.y}")
                } else {
                    state = State.SWIPE
                    touchMove(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                timer.cancel()
                touchUp()
            }
        }
        return true
    }

    private var start = 0L
    private fun touchDown(event: MotionEvent) {
        start = System.currentTimeMillis()
        timer.start()
    }

    private fun touchUp() {
        timer.cancel()
        state = State.IDLE
        takeAction()
        val cx = stickPoint.x
        val cy = stickPoint.y
        resetAnimator.addUpdateListener {
            val value = resetAnimator.animatedValue as Float
            stickPoint = PointF(
                (1f - value) * cx + value * cp,
                (1f - value) * cy + value * cp
            )
            postInvalidate()
        }
        resetAnimator.start()
    }

    private fun touchMove(x: Float, y: Float) {
        timer.cancel()

        if (resetAnimator.isRunning) {
            resetAnimator.cancel()
        }
        stickPoint.x = x
        stickPoint.y = y

        computeDistance().also {
            capStick(it)
        }

        invalidate()
    }

    private fun takeAction() {
        if (computeDistance() < maxDistance) {
            listener?.invoke(Direction.CENTER)
            return
        }

        when (computeAngle()) {
            in 0..45, in 316..360 -> listener?.invoke(Direction.UP)
            in 46..135 -> listener?.invoke(Direction.RIGHT)
            in 136..225 -> listener?.invoke(Direction.BOTTOM)
            in 226..315 -> listener?.invoke(Direction.LEFT)
        }
    }

    private fun capStick(distance: Float): Boolean {
        if (distance < maxDistance) return false

        stickPoint.x = (stickPoint.x - cp) * maxDistance / distance + cp
        stickPoint.y = (stickPoint.y - cp) * maxDistance / distance + cp

        return true
    }

    private fun computeDistance() =
        sqrt(
            (stickPoint.x - cp).pow(2) + (stickPoint.y - cp).pow(2)
        )

    private fun computeAngle(): Int {
        val angle = Math.toDegrees(
            atan2(
                x = (cp - stickPoint.y).toDouble(),
                y = (stickPoint.x - cp).toDouble()
            )
        )
        return if (angle < 0) {
            angle.toInt() + 360
        } else {
            angle.toInt()
        }
    }
}