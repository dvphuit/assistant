//package dvp.app.assistant.services.views.joystick
//
//import android.animation.ValueAnimator
//import android.graphics.PointF
//import android.os.CountDownTimer
//import android.util.Log
//import android.view.MotionEvent
//import android.view.WindowManager
//import android.view.animation.BounceInterpolator
//import kotlin.math.atan2
//import kotlin.math.pow
//import kotlin.math.sqrt
//
//class TouchHandler(
//    private var stickPoint: PointF,
//    private val windowManager: WindowManager,
//    private val params: WindowManager.LayoutParams
//) {
//
//    private var state = JSState.IDLE
//    private var initialX = 0
//    private var initialY = 0
//    private var initialTouchX = 0f
//    private var initialTouchY = 0f
//    private val resetAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
//        duration = 100
//        interpolator = BounceInterpolator()
//    }
//
//    private val timer = object : CountDownTimer(300, 300) {
//        override fun onTick(millisUntilFinished: Long) {
//
//        }
//
//        override fun onFinish() {
//            Log.d("TEST", "Timer finished")
//            state = JSState.HELD
//        }
//    }
//
//    fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                timer.start()
//                initialX = params.x
//                initialY = params.y
//                initialTouchX = event.rawX
//                initialTouchY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (state == JSState.HELD) {
//                    params.x = initialX + (event.rawX - initialTouchX).toInt()
//                    params.y = initialY + (event.rawY - initialTouchY).toInt()
//                    windowManager.updateViewLayout(this, params)
//                } else {
//                    state = JSState.SWIPE
//                    timer.cancel()
//                    moveStick(event.x, event.y)
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                state = JSState.IDLE
//                timer.cancel()
//                touchUp()
//            }
//        }
//        return true
//    }
//
//    private fun touchUp() {
//        fireAction()
//        val cx = stickPoint.x
//        val cy = stickPoint.y
//        resetAnimator.addUpdateListener {
//            val value = resetAnimator.animatedValue as Float
//            stickPoint = PointF(
//                (1f - value) * cx + value * cp,
//                (1f - value) * cy + value * cp
//            )
//            postInvalidate()
//        }
//        resetAnimator.start()
//    }
//
//    private fun moveStick(x: Float, y: Float) {
//
//        if (resetAnimator.isRunning) {
//            resetAnimator.cancel()
//        }
//        stickPoint.x = x
//        stickPoint.y = y
//
//        computeDistance().also {
//            capStick(it)
//        }
//
//        invalidate()
//    }
//
//    private fun fireAction() {
//        if (computeDistance() < maxDistance) {
//            directionListener?.invoke(JSDirection.CENTER)
//            return
//        }
//
//        when (computeAngle()) {
//            in 0..45, in 316..360 -> directionListener?.invoke(JSDirection.UP)
//            in 46..135 -> directionListener?.invoke(JSDirection.RIGHT)
//            in 136..225 -> directionListener?.invoke(JSDirection.BOTTOM)
//            in 226..315 -> directionListener?.invoke(JSDirection.LEFT)
//        }
//    }
//
//    private fun capStick(distance: Float): Boolean {
//        if (distance < maxDistance) return false
//
//        stickPoint.x = (stickPoint.x - cp) * maxDistance / distance + cp
//        stickPoint.y = (stickPoint.y - cp) * maxDistance / distance + cp
//
//        return true
//    }
//
//    private fun computeDistance() =
//        sqrt((stickPoint.x - cp).pow(2) + (stickPoint.y - cp).pow(2))
//
//    private fun computeAngle(): Int {
//        val angle = Math.toDegrees(
//            atan2(
//                x = (cp - stickPoint.y).toDouble(),
//                y = (stickPoint.x - cp).toDouble()
//            )
//        )
//        return if (angle < 0) {
//            angle.toInt() + 360
//        } else {
//            angle.toInt()
//        }
//    }
//}