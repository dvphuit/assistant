package dvp.app.assistant.screenshot

import android.graphics.Bitmap

sealed class Result {
    class Success(val bitmap: Bitmap) : Result()
    class Error(val message: String?) : Result()
}
