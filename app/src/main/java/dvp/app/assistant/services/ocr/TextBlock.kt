package dvp.app.assistant.services.ocr

import android.graphics.Rect

data class TextBlock( var rect: Rect, val src: String, var trans: String? = null, val lines: Int = 1)