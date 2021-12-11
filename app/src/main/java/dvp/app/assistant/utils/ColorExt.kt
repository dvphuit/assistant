package dvp.app.assistant.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * @author PhuDV
 * Created 8/15/21
 */


@Stable
fun Color.asNativeColor(): Int {
    return android.graphics.Color.argb(alpha, red, green, blue)
}

fun Color.getContrast(): Color {
    val y: Float = (299 * this.red + 587 * this.green + 114 * this.blue) / 1000
    return if (y >= 128) Color.Black else Color.White
}