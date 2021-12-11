package dvp.app.assistant.services.views.transition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class Scale : Visibility() {
    companion object {
        private const val PROPNAME_TRANSITION_SCALE_X = "dev.jegul.transition:scale:scaleX"
        private const val PROPNAME_TRANSITION_SCALE_Y = "dev.jegul.transition:scale:scaleY"
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        transitionValues.values[PROPNAME_TRANSITION_SCALE_X] = transitionValues.view.scaleX
        transitionValues.values[PROPNAME_TRANSITION_SCALE_Y] = transitionValues.view.scaleY
    }

    override fun onAppear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        val startScaleX = getStartScaleValue(startValues, PROPNAME_TRANSITION_SCALE_X, 0f)
        val startScaleY = getStartScaleValue(startValues, PROPNAME_TRANSITION_SCALE_Y, 0f)

        return createScaleAnimation(
            view = view,
            startScaleX = if (startScaleX == 1f) 0f else startScaleX,
            startScaleY = if (startScaleY == 1f) 0f else startScaleY,
            endScaleX = 1f,
            endScaleY = 1f
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {
        val startScaleX = getStartScaleValue(startValues, PROPNAME_TRANSITION_SCALE_X, 1f)
        val startScaleY = getStartScaleValue(startValues, PROPNAME_TRANSITION_SCALE_Y, 1f)

        return createScaleAnimation(
            view = view,
            startScaleX = startScaleX,
            startScaleY = startScaleY,
            endScaleX = 0f,
            endScaleY = 0f
        )
    }

    private fun createScaleAnimation(
        view: View?,
        startScaleX: Float,
        startScaleY: Float,
        endScaleX: Float,
        endScaleY: Float
    ): Animator {
        val animScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, startScaleX, endScaleX)
        val animScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScaleY, endScaleY)

        val animator = AnimatorSet()
        animator.playTogether(animScaleX, animScaleY)

        view?.let {
            animator.addListener(getAnimatorListener(it))
            addListener(getTransitionListener(it))
        }

        return animator
    }

    private fun getStartScaleValue(
        scaleValue: TransitionValues?,
        propName: String,
        fallbackValue: Float
    ): Float {
        return scaleValue?.values?.get(propName) as? Float ?: fallbackValue
    }

    private fun getAnimatorListener(view: View): AnimatorListenerAdapter {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                doOnEnd(view)
            }
        }
    }

    private fun getTransitionListener(view: View): TransitionListenerAdapter {
        return object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                doOnEnd(view)
            }
        }
    }

    private fun doOnEnd(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
    }
}