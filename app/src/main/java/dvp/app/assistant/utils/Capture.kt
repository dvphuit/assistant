package dvp.app.assistant.utils

import android.app.Activity
import android.util.Log
import dvp.app.assistant.screenshot.Result
import dvp.app.assistant.screenshot.ScreenShot

object Capture {
    var screenShot: ScreenShot? = null
        private set

    fun init(activity: Activity) {
        screenShot = ScreenShot(activity)
    }

    fun screenShoot() {
        screenShot!!
            .makeScreenshot()
            .observe {
                when (it) {
                    is Result.Success -> {
                        Log.d("TEST", "result $it")
                    }
                    is Result.Error -> {
                        Log.e("TEST", "error $it")
                    }
                }
            }
    }

}