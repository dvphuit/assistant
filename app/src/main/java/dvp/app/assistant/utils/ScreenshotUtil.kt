package dvp.app.assistant.utils

import android.app.Activity
import android.graphics.Bitmap
import android.view.View

object ScreenshotUtil {
    /**
     * Measures and takes a screenshot of the provided [View].
     *
     * @param view The view of which the screenshot is taken
     * @return A [Bitmap] for the taken screenshot.
     */
    fun takeScreenshotForView(view: View): Bitmap {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY)
        )
        view.layout(
            view.x.toInt(),
            view.y.toInt(),
            view.x.toInt() + view.measuredWidth,
            view.y.toInt() + view.measuredHeight
        )
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        val bitmap: Bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    fun takeScreenshotForScreen(activity: Activity): Bitmap {
        return takeScreenshotForView(activity.window.decorView.rootView)
    }

}