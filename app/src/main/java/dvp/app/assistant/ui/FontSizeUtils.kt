package dvp.app.assistant.ui

import android.graphics.Rect
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils

class FontSizeUtils() {
    var sizeTester: SizeTester? = null

    class SizeTester(var minTextSize: Float, var maxTextSize: Float) {
        var padding = 0f
    }

    fun getFontSize(
        text: String,
        isSingleLine: Boolean,
        availableSpace: RectF,
        tp: TextPaint
    ): Int {
        val max = if (sizeTester == null) MAX_TEXT_SIZE else sizeTester!!.maxTextSize
        val min = if (sizeTester == null) MIN_TEXT_SIZE else sizeTester!!.minTextSize
        val start = min.toInt()
        var lastBest = start
        var hi = max.toInt()
        var lo = start
        var mid: Int
        while (hi - lo > 1) {
            mid = lo + hi ushr 1
            tp.textSize = mid.toFloat()
            val flag = testFontSize(text, availableSpace, tp, isSingleLine)
            if (flag) {
                lastBest = lo
                lo = mid + 1
            } else {
                hi = mid - 1
                lastBest = hi
            }
        }
        return lastBest
    }

    companion object {
        private const val MAX_TEXT_SIZE = 100.0f
        private const val MIN_TEXT_SIZE = 8.0f
        private fun testFontSize(
            text: String,
            availableSpace: RectF,
            tp: TextPaint,
            singleLine: Boolean
        ): Boolean {
            return  /*singleLine ?
                testFontSizeSingleline(text, availableSpace, tp) :*/testMultilineSize(
                text,
                availableSpace,
                tp
            )
        }

        private fun testMultilineSize(text: String, availableSpace: RectF, tp: TextPaint): Boolean {
            val sl = StaticLayout(
                text,
                tp,
                availableSpace.width().toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                true
            )
            val lineHeight = tp.fontSpacing
            val textRect = RectF()
            val maxHeight = sl.height
            textRect.bottom = maxHeight.toFloat()
            var maxWidth = -1
            for (i in 0 until sl.lineCount) if (maxWidth < sl.getLineRight(i) - sl.getLineLeft(i)) maxWidth =
                sl.getLineRight(i)
                    .toInt() - sl.getLineLeft(i).toInt()
            maxWidth += 5 //hack for Nexus 7 https://st.yandex-team.ru/MT-2386
            textRect.right = maxWidth.toFloat()
            textRect.offsetTo(0f, 0f)


            //bug; we need to know the longest word, as it should not have any breaks
            val word = StringUtils.getLongestWord(text) + text[0] //huck
            val measureWidth = tp.measureText(word)
            val boundsLWord = Rect()
            tp.getTextBounds(word, 0, word.length, boundsLWord)
            val avHeight = availableSpace.height()
            val avWidth = availableSpace.width()


            //check if longest word fits width
            val t = TextUtils.ellipsize(word, tp, avWidth, TextUtils.TruncateAt.END)
            if (!TextUtils.equals(word, t)) { //if we have a truncated text
                return false // too big
            }
            return !(maxHeight >= avHeight || maxWidth >= avWidth || measureWidth >= avWidth || boundsLWord.height() >= avHeight || boundsLWord.width() >= avWidth)
        }

        private fun testFontSizeSingleline(
            text: String,
            availableSpace: RectF,
            tp: TextPaint
        ): Boolean {
            val bounds = Rect()
            val lineHeight = tp.fontSpacing
            val measureWidth = tp.measureText(text)
            tp.getTextBounds(text, 0, text.length, bounds)
            return !(measureWidth >= availableSpace.width() || lineHeight >= availableSpace.height() || bounds.height() >= availableSpace.height())
            // too small
        }
    }
}