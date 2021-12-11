package dvp.app.assistant.services.views.transition

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Path
import android.graphics.Rect
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.transition.ChangeBounds
import androidx.transition.TransitionValues


class BubbleTextTransition : ChangeBounds() {

    companion object {
        private const val PROP_NAME_BOUNDS = "android:changeBounds:bounds"
        private const val PROP_NAME_PARENT = "android:changeBounds:parent"
    }

    private var startRect: Rect? = null
    private var endRect: Rect? = null
    private var startColor: Int = -1
    private var endColor: Int = -1
    private var pathData: Path? = null

    fun setStartBound(rect: Rect) {
        this.startRect = rect
    }

    fun setEndBound(rect: Rect) {
        this.endRect = rect
    }

    fun setBackground(startColor: Int, endColor: Int, path: Path) {
        this.startColor = startColor
        this.endColor = endColor
        this.pathData = path
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        transitionValues.values[PROP_NAME_BOUNDS] = startRect
        transitionValues.values[PROP_NAME_PARENT] = transitionValues.view.parent
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        transitionValues.values[PROP_NAME_BOUNDS] = endRect
        transitionValues.values[PROP_NAME_PARENT] = transitionValues.view.parent
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        val changeBounds: Animator? = super.createAnimator(sceneRoot, startValues, endValues)
        if (startValues == null || endValues == null || changeBounds == null) return null

        val background = PathDrawable(startColor, pathData!!)
        endValues.view.background = background
        val colorAnimator = ObjectAnimator.ofArgb(background, updateDrawableColor(), endColor)

        return AnimatorSet().apply {
            playTogether(changeBounds, colorAnimator)
            duration = 300
            interpolator = AnimationUtils.loadInterpolator(
                sceneRoot.context,
                android.R.interpolator.fast_out_slow_in
            )
        }
    }

}