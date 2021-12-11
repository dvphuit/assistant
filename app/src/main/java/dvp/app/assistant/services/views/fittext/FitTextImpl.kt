package dvp.app.assistant.services.views.fittext

import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import dvp.app.assistant.services.ocr.TextBlock
import dvp.app.assistant.services.views.IDraw

class FitTextImpl(private val parent: View) : IDraw {

    companion object {
        private const val NO_LINE_LIMIT = -1
        private const val MAX_LINES = NO_LINE_LIMIT
        private const val mMinTextSize = 20f
        private var mMaxTextSize = 300f
    }

    private var mTextCachedSizes: SparseIntArray = SparseIntArray()
    private var mAvailableSpaceRect: RectF = RectF()
    private val mTextRect = RectF()
    private val mSpacingMul = 1.0f
    private val mSpacingAdd = 0.0f
    private var mEnableSizeCache = false

    private val textPaint = TextPaint().apply {
        color = Color.RED
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
        textSize = 100f
    }

    override fun setTextBlocks(textBlocks: List<TextBlock>) {

    }

    override fun draw(canvas: Canvas, text: String, rect: Rect) {
        val sl = textPaint.run {
            textSize = getFitTextSize(text, rect)
            makeTextLayout(text, this, rect.width())
        }
        canvas.withTranslation(rect.left.toFloat(), rect.top.toFloat()) {
            sl.draw(canvas)
        }
    }

    override fun onTouch(event: MotionEvent) {

    }

    override fun close() {

    }

    private fun getFitTextSize(text: String, rect: Rect): Float {
        val startSize = mMinTextSize.toInt()
        mAvailableSpaceRect.right = rect.width().toFloat()
        mAvailableSpaceRect.bottom = rect.height().toFloat()
        return efficientTextSizeSearch(
            text,
            rect,
            startSize,
            mMaxTextSize.toInt(),
            mAvailableSpaceRect
        ).toFloat()
    }

    private fun makeTextLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width)
            .setIncludePad(false)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(mSpacingAdd, mSpacingMul)
            .build()
    }

    private fun testFontSize(
        text: String,
        rect: Rect,
        suggestedSize: Int,
        availableSpace: RectF
    ): Int {
        textPaint.textSize = suggestedSize.toFloat()
        if (MAX_LINES == 1) {
            mTextRect.bottom = textPaint.fontSpacing
            mTextRect.right = textPaint.measureText(text)
        } else {
            val layout = makeTextLayout(text, textPaint, rect.width())
            // return early if we have more lines
            if (MAX_LINES != NO_LINE_LIMIT && layout.lineCount > MAX_LINES) {
                return 1
            }
            mTextRect.bottom = layout.height.toFloat()
            var maxWidth = -1
            for (i in 0 until layout.lineCount) {
                if (maxWidth < layout.getLineWidth(i)) {
                    maxWidth = layout.getLineWidth(i).toInt()
                }
            }
            mTextRect.right = maxWidth.toFloat()
        }
        mTextRect.offsetTo(0f, 0f)
        return if (availableSpace.contains(mTextRect)) -1 //too small
        else 1 //too big
    }

    private fun efficientTextSizeSearch(
        text: String,
        rect: Rect,
        start: Int,
        end: Int,
        availableSpace: RectF
    ): Int {
        if (!mEnableSizeCache) {
            return binarySearch(text, rect, start, end, availableSpace)
        }
        val key = text.length
        var size = mTextCachedSizes[key]
        if (size != 0) {
            return size
        }
        size = binarySearch(text, rect, start, end, availableSpace)
        mTextCachedSizes.put(key, size)
        return size
    }

    private fun binarySearch(
        text: String,
        rect: Rect,
        start: Int,
        end: Int,
        availableSpace: RectF
    ): Int {
        var lastBest = start
        var lo = start
        var hi = end - 1
        var mid: Int
        while (lo <= hi) {
            mid = lo + hi ushr 1
            val midValCmp = testFontSize(text, rect, mid, availableSpace)
            if (midValCmp < 0) {
                lastBest = lo
                lo = mid + 1
            } else if (midValCmp > 0) {
                hi = mid - 1
                lastBest = hi
            } else {
                return mid
            }
        }
        return lastBest
    }

}