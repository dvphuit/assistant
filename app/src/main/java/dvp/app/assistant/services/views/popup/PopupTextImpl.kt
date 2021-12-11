package dvp.app.assistant.services.views.popup

import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRectF
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.services.views.IDraw
import dvp.app.assistant.utils.dp

class PopupTextImpl(private val parent: View) : IDraw {

    companion object {
        private val roundedRadius = 5.dp
        private val padding = 4.dp
        private val bgPaint = Paint().apply {
            color = Color.parseColor("#22000000")
            style = Paint.Style.FILL
        }
        private val textPaint = TextPaint().apply {
            this.color = Color.BLACK
            this.isAntiAlias = true
            this.textSize = 16.dp
        }
        private val bgPopupPaint = Paint().apply {
            this.color = Color.RED
            this.style = Paint.Style.FILL
        }
    }

    private var isShowingPopup = false
    private var canvas: Canvas? = null

    private var textBlocks: List<TextBlock> = emptyList()

    override fun setTextBlocks(textBlocks: List<TextBlock>) {
        this.textBlocks = textBlocks
    }

    override fun draw(canvas: Canvas, text: String, rect: Rect) {
        this.canvas = canvas
        canvas.drawRoundRect(rect.padding(), roundedRadius, roundedRadius, bgPaint)
    }

    private fun Rect.padding(dp: Float = padding): RectF {
        return this.toRectF().apply {
            this.top -= dp
            this.left -= dp
            this.bottom += dp
            this.right += dp
        }
    }

    override fun onTouch(event: MotionEvent) {
        val found = textBlocks.find { it.rect.contains(event.x.toInt(), event.y.toInt()) }
            ?: return

        Log.d("TEST", "Show ${found.rect} - ${found.src} - ${found.trans}")
        this.showPopup(found.trans ?: "Empty", found.rect)
    }

    private fun showPopup(text: String, rect: Rect) {
        this.canvas?.withTranslation(rect.left.toFloat(), rect.top.toFloat()){
            val textLayout = makeTextLayout(text, textPaint, rect.width())
            val textBounds = getTextBound(textLayout)
            this.drawRoundRect(textBounds.padding(), roundedRadius, roundedRadius, bgPopupPaint)
            textLayout.draw(this)
            parent.invalidate()
        }
    }

    private fun getTextBound(textLayout: StaticLayout): Rect {
        val textBounds = getLineBounds(textLayout, 0)
        var lineBounds = getLineBounds(textLayout, textLayout.lineCount - 1)
        if (lineBounds.bottom > textBounds.bottom) {
            textBounds.bottom = lineBounds.bottom
        }
        for (line in 0 until textLayout.lineCount) {
            lineBounds = getLineBounds(textLayout, line)
            if (lineBounds.left < textBounds.left) {
                textBounds.left = lineBounds.left
            }
            if (lineBounds.right > textBounds.right) {
                textBounds.right = lineBounds.right
            }
        }
        return textBounds
    }

    private fun getLineBounds(textLayout: StaticLayout, line: Int): Rect {
        val bounds = Rect()
        val firstCharOnLine: Int = textLayout.getLineStart(line)
        val lastCharOnLine: Int = textLayout.getLineVisibleEnd(line)
        val s = textLayout.text.substring(firstCharOnLine, lastCharOnLine)
        textPaint.getTextBounds(s, 0, s.length, bounds)
        val baseline: Int = textLayout.getLineBaseline(line)
        bounds.top = baseline + bounds.top
        bounds.bottom = baseline + bounds.bottom
        return bounds
    }

    private fun makeTextLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setIncludePad(true)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .build()
    }

    override fun close() {
        isShowingPopup = false
    }

}