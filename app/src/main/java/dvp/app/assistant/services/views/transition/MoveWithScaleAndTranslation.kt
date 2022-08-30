package dvp.app.assistant.services.views.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.ViewGroup
import androidx.core.animation.doOnStart
import androidx.transition.Transition
import androidx.transition.TransitionValues

class MoveWithScaleAndTranslation : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun getTransitionProperties() = properties

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        val values = transitionValues.values

        val screenLocation = IntArray(2)
        view.getLocationOnScreen(screenLocation)
        values[PROPNAME_POSX] = screenLocation[0]
        values[PROPNAME_POSY] = screenLocation[1]

        values[PROPNAME_WIDTH] = view.width
        values[PROPNAME_HEIGHT] = view.height
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if(startValues == null || endValues == null)
            return null

        val leftDelta = ((startValues.values[PROPNAME_POSX] as Int) - (endValues.values[PROPNAME_POSX] as Int)).toFloat()
        val topDelta = ((startValues.values[PROPNAME_POSY] as Int) - (endValues.values[PROPNAME_POSY] as Int)).toFloat()

        val scaleWidth = (startValues.values[PROPNAME_WIDTH] as Int).toFloat() / (endValues.values[PROPNAME_WIDTH] as Int).toFloat()
        val scaleHeight = (startValues.values[PROPNAME_HEIGHT] as Int).toFloat() / (endValues.values[PROPNAME_HEIGHT] as Int).toFloat()

        val view = endValues.view
        val anim = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", scaleWidth, 1f),
                PropertyValuesHolder.ofFloat("scaleY", scaleHeight, 1f),
                PropertyValuesHolder.ofFloat("translationX", leftDelta, 0f),
                PropertyValuesHolder.ofFloat("translationY", topDelta, 0f)
        )
        anim.doOnStart {
            view.pivotX = 0f
            view.pivotY = 0f
        }
        return anim
    }

    companion object {
        private const val PROPNAME_POSX = "movewithscaleandtranslation:posX"
        private const val PROPNAME_POSY = "movewithscaleandtranslation:posY"
        private const val PROPNAME_WIDTH = "movewithscaleandtranslation:width"
        private const val PROPNAME_HEIGHT = "movewithscaleandtranslation:height"
        val properties = arrayOf(PROPNAME_POSX, PROPNAME_POSY, PROPNAME_WIDTH, PROPNAME_HEIGHT)
    }
}