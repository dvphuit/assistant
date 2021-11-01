package dvp.app.assistant.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import dvp.app.assistant.utils.Capture
import kotlin.math.abs

@Composable
fun ButtonTest() {
    ExtendedFloatingActionButton(
        onClick = { /* ... */ },
        icon = {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite"
            )
        },
        text = { Text("Like") }
    )
}

class OverlayService : Service(), View.OnClickListener {

    private var btOverlay: JoystickView? = null
    private var offsetX = 0f
    private var offsetY = 0f
    private var originalXPos = 0
    private var originalYPos = 0
    private var moving = false
//    private var wm: WindowManager? = null

    private val wm by lazy {
        getSystemService(Context.WINDOW_SERVICE)
                as WindowManager
    }

    private val nm by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        createNotification()

        val params = WindowManager.LayoutParams(
            200,
            200,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.START or Gravity.TOP
        params.x = 0
        params.y = 0
//

        btOverlay = JoystickView(this)
        btOverlay?.setDirectionListener {
            Log.d("TEST", "joystick -> ${it.name}")
        }
        wm.apply {
            addView(btOverlay, params)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        val channelId = "my_channel_01"
        val channel = NotificationChannel(
            "my_channel_01",
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nm.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("")
            .setContentText("").build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (btOverlay != null) {
            wm.removeView(btOverlay)
            btOverlay = null
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouch(v: View?, event: MotionEvent): Boolean {
//        if (event.action == MotionEvent.ACTION_DOWN) {
//            val x = event.rawX
//            val y = event.rawY
//            moving = false
//            val location = IntArray(2)
//            btOverlay!!.getLocationOnScreen(location)
//            originalXPos = location[0]
//            originalYPos = location[1]
//            offsetX = originalXPos - x
//            offsetY = originalYPos - y
//        } else if (event.action == MotionEvent.ACTION_MOVE) {
//            val topLeftLocationOnScreen = IntArray(2)
//            btOverlay?.getLocationOnScreen(topLeftLocationOnScreen)
////            println("topLeftY=" + topLeftLocationOnScreen[1])
////            println("originalY=$originalYPos")
//            val x = event.rawX
//            val y = event.rawY
//            val params: WindowManager.LayoutParams = btOverlay!!.layoutParams as WindowManager.LayoutParams
//            val newX = (offsetX + x).toInt()
//            val newY = (offsetY + y).toInt()
//            if (abs(newX - originalXPos) < 1 && abs(newY - originalYPos) < 1 && !moving) {
//                return false
//            }
//            params.x = newX - topLeftLocationOnScreen[0]
//            params.y = newY - topLeftLocationOnScreen[1]
//            wm!!.updateViewLayout(btOverlay, params)
//            moving = true
//        } else if (event.action == MotionEvent.ACTION_UP) {
//            if (moving) {
//                return true
//            }
//        }
//        return false
//    }

    override fun onClick(v: View?) {
        Toast.makeText(this, "Shoot", Toast.LENGTH_SHORT).show()
        Capture.screenShoot()
    }

}