package dvp.app.assistant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View

@SuppressLint("AppCompatCustomView")
class AutoSizeText : View {

    private var mPaint: TextPaint = TextPaint().apply {
        this.isAntiAlias = true
        this.textSize = 250f
    }

    private var mTextCachedSizes: SparseIntArray? = null
    private var mAvailableSpaceRect: RectF = RectF()
    private val mTextRect = RectF()
    private val mMinTextSize = 10f
    private var mMaxTextSize = 100f
    private val mSpacingMul = 1.0f
    private val mSpacingAdd = 0.0f
    private var mMaxLines = NO_LINE_LIMIT
    private var mEnableSizeCache = true
    private var text = ""
    private var rect = RectF()

    private val listText = listOf(
        "Are\nyou\nlooking",
        "Are you looking for the\nBest Courses to\nLearn Deep Learning Learn Deep Learning",
        "Are you looking for the Best Courses to Learn Deep Learning? If yes, then you are in the right place. In this article"
    )

    private val listRect = listOf(
        Rect(0, 0, 400, 300),
        Rect(420, 110, 800, 300),
        Rect(900, 50, 1200, 400),
    )

    constructor(context: Context?) : super(context) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialize()
    }

    private fun initialize() {
        mMaxTextSize = mPaint.textSize
        mTextCachedSizes = SparseIntArray()
    }

    private val bgPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        listText.forEachIndexed { index, s ->
            text = s
            rect.set(listRect[index])
            canvas.drawRect(rect, bgPaint)

            drawText(canvas, listRect[index])
        }
    }

    private fun drawText(canvas: Canvas, rect: Rect) {
        val sl = mPaint.run {
            textSize = getFitTextSize(rect)
            makeTextLayout(text, mPaint, rect.width())
        }
        canvas.save()
        canvas.translate(rect.left.toFloat(), rect.top.toFloat())
        sl.draw(canvas)
        canvas.restore()
    }

    private fun getFitTextSize(rect: Rect): Float {
        val startSize = mMinTextSize.toInt()
        mAvailableSpaceRect.right = rect.width().toFloat()
        mAvailableSpaceRect.bottom = rect.height().toFloat()
        return efficientTextSizeSearch(
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

    private fun testFontSize(suggestedSize: Int, availableSpace: RectF): Int {
        mPaint.textSize = suggestedSize.toFloat()
        if (mMaxLines == 1) {
            mTextRect.bottom = mPaint.fontSpacing
            mTextRect.right = mPaint.measureText(text)
        } else {
            val layout = makeTextLayout(text, mPaint, rect.width().toInt())
            // return early if we have more lines
            if (mMaxLines != NO_LINE_LIMIT && layout.lineCount > mMaxLines) {
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
        start: Int,
        end: Int,
        availableSpace: RectF
    ): Int {
        if (!mEnableSizeCache) {
            return binarySearch(start, end, availableSpace)
        }
        val key = text.length
        var size = mTextCachedSizes!![key]
        if (size != 0) {
            return size
        }
        size = binarySearch(start, end, availableSpace)
        mTextCachedSizes!!.put(key, size)
        return size
    }

    private fun binarySearch(
        start: Int,
        end: Int,
        availableSpace: RectF
    ): Int {
        var lastBest = start
        var lo = start
        var hi = end - 1
        var mid = 0
        while (lo <= hi) {
            mid = lo + hi ushr 1
            val midValCmp = testFontSize(mid, availableSpace)
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

    companion object {
        private const val NO_LINE_LIMIT = -1
    }
}