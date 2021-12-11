package dvp.app.assistant.services.views.transition

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.IntProperty
import android.util.Property
import androidx.annotation.ColorInt
import androidx.core.graphics.toRectF
import dvp.app.assistant.utils.dp

class PathDrawable(@ColorInt color: Int, val pathData: Path) : ColorDrawable() {

    init {
        paint.color = color
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(pathData, paint)
    }
}

class RectDrawable(@ColorInt color: Int, val rect: Rect) : ColorDrawable() {

    init {
        paint.color = color
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rect.toRectF(), 5.dp, 5.dp, paint)
    }
}


fun <T: ColorDrawable> updateDrawableColor(): Property<T, Int> = object : IntProperty<T>("color") {
    override fun setValue(morphDrawable: T, value: Int) {
        morphDrawable.color = value
    }

    override operator fun get(morphDrawable: T): Int {
        return morphDrawable.color
    }
}

abstract class ColorDrawable: Drawable(){
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var color: Int
        get() = paint.color
        set(color) {
            paint.color = color
            invalidateSelf()
        }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return paint.alpha
    }
}