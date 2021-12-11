package dvp.app.assistant.base.ext

import android.content.Context
import android.view.WindowManager
import android.widget.Toast

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.getWindowManager(): WindowManager {
    return this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}