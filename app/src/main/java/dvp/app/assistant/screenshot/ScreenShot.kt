package dvp.app.assistant.screenshot

import android.app.Activity
import android.content.Intent
import android.util.Log
import dvp.app.assistant.screenshot.internal.MediaProjectionDelegate

class ScreenShot(activity: Activity) {
    private val mediaProjectionDelegate = MediaProjectionDelegate(activity, 9999)

    fun makeScreenshot(): ScreenshotResult = mediaProjectionDelegate.shot()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediaProjectionDelegate.onActivityResult(requestCode, resultCode, data)
    }
}
