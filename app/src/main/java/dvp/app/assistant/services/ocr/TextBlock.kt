package dvp.app.assistant.services.ocr

import android.graphics.Rect

data class TextBlock(val rect: Rect, val src: String, var trans: String? = null)