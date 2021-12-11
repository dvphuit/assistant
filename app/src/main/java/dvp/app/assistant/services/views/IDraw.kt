package dvp.app.assistant.services.views

import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import dvp.app.assistant.services.ocr.TextBlock

interface IDraw {
    fun setTextBlocks(textBlocks: List<TextBlock>)
    fun draw(canvas: Canvas, text: String, rect: Rect)
    fun onTouch(event: MotionEvent)
    fun close()
}