package dvp.app.assistant.services.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.View
import androidx.core.graphics.toRectF
import androidx.core.view.setPadding
import dvp.app.assistant.utils.dp

class DetectView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val roundedRadius = 5.dp
    private val padding = 4.dp.toInt()
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#22000000")
        style = Paint.Style.FILL
    }
    private lateinit var size: Size
    private var mRect: Rect = Rect(0, 0, 0, 0)

    fun setSize(width: Int, height: Int) {
//        this.size = size
        mRect = Rect(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        mRect.set(0, 0, width, height)
        canvas.drawRoundRect(
            mRect.toRectF(),
            roundedRadius,
            roundedRadius,
            bgPaint
        )
    }

//    private fun Rect.padding(dp: Float = padding) = this.toRectF().apply {
//        this.top -= dp
//        this.left -= dp
//        this.bottom += dp
//        this.right += dp
//    }
}