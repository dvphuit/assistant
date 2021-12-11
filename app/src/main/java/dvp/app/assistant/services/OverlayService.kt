package dvp.app.assistant.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dvp.app.assistant.base.ext.showToast
import dvp.app.assistant.services.views.DetectionView
import dvp.app.assistant.services.views.joystick.JSDirection
import dvp.app.assistant.services.views.joystick.JoystickView


class OverlayService : Service() {

    private lateinit var btOverlay: JoystickView

    private val nm by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        createNotification()

        DetectionView(this)
            .also(Capture::setOverlay)


        btOverlay = JoystickView(this).apply {
            setDirectionListener {
                Log.d("TEST", "joystick -> ${it.name}")
                when (it) {
                    JSDirection.CENTER -> showToast(it.name)
                    JSDirection.UP -> showToast(it.name)
                    JSDirection.LEFT -> showToast(it.name)
                    JSDirection.RIGHT -> showToast(it.name)
                    JSDirection.BOTTOM -> {
                        showToast(it.name)
                        Capture.screenShoot()
                    }
                }
            }
        }
    }

    private fun createNotification() {
        val channelId = "capture"
        val channel = NotificationChannel(
            channelId,
            "Title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nm.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSilent(true)
            .setContentTitle("")
            .setContentText("").build()
        startForeground(channelId.hashCode(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        btOverlay.onDestroy()
    }
}