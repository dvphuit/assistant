package dvp.app.assistant.services.joystick

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import androidx.annotation.RequiresApi
import dvp.app.assistant.services.joystick.JSDirection
import dvp.app.assistant.services.joystick.JSState
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewConstructor")
class JoystickView constructor(context: Context) : View(context) {

    private val wm by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var state = JSState.IDLE

    private val timer = object : CountDownTimer(300, 300) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            Log.d("TEST", "Timer finished")
            state = JSState.HELD
        }
    }

    private var stickPoint = PointF(0f, 0f)
    private var cp = 0f
    private var maxDistance = 0f
    private var rStick = 0f
    private var rBase = 0f
    private  var params: WindowManager.LayoutParams

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

    private var directionListener: ((JSDirection) -> Unit)? = null

    fun setDirectionListener(listener: (JSDirection) -> Unit) {
        this.directionListener = listener
    }

    private val resetAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100
        interpolator = BounceInterpolator()
    }

    init {
        params = createButtonLayoutParams()
        wm.addView(this, params)
    }

    private fun createButtonLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            100, 100,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = 0
            y = 0
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.stickPoint = PointF(w.toFloat() / 2, h.toFloat() / 2)
        this.cp = min(w, h) / 2f
        this.rBase = w / 2f - 10
        this.rStick = this.rBase * .9f
        this.maxDistance = this.rBase - this.rStick + 10
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(cp, cp, rBase, basePaint)
        canvas.drawCircle(stickPoint.x, stickPoint.y, rStick, stickPaint)
    }

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                timer.start()
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == JSState.HELD) {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    wm.updateViewLayout(this, params)
                } else {
                    state = JSState.SWIPE
                    timer.cancel()
                    moveStick(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                state = JSState.IDLE
                timer.cancel()
                touchUp()
            }
        }
        return true
    }

    private fun touchUp() {
        fireAction()
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

    private fun moveStick(x: Float, y: Float) {

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

    private fun fireAction() {
        if (computeDistance() + 10 < maxDistance) {
//            directionListener?.invoke(JSDirection.CENTER)
            return
        }

        when (computeAngle()) {
            in 0..45, in 316..360 -> directionListener?.invoke(JSDirection.UP)
            in 46..135 -> directionListener?.invoke(JSDirection.RIGHT)
            in 136..225 -> directionListener?.invoke(JSDirection.BOTTOM)
            in 226..315 -> directionListener?.invoke(JSDirection.LEFT)
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

    fun onDestroy() {
        wm.removeView(this)
    }

}