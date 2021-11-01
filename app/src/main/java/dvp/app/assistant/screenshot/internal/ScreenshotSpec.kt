package dvp.app.assistant.screenshot.internal

import android.app.Activity
import android.graphics.Point
import android.view.Display

class ScreenshotSpec constructor(activity: Activity) {

    val width: Int
    val height: Int
    val densityDpi: Int

    init {
        val display = activity.windowManager.defaultDisplay
        val displaySize = getDisplaySize(display)
        width = displaySize.x
        height = displaySize.y
        densityDpi = activity.resources.displayMetrics.densityDpi
    }

    private fun getDisplaySize(display: Display): Point {
        return Point().apply {
            display.getRealSize(this)
        }
    }
}