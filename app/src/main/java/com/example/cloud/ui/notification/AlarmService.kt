package com.example.cloud.ui.notification

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.cloud.R

class AlarmService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var mediaPlayer: MediaPlayer? = null
    private var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Play the alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.mixkit_classical_vibes)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        showAlarmOverlay()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return START_STICKY
    }

    private fun showAlarmOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutInflater = LayoutInflater.from(this)

        overlayView = layoutInflater.inflate(R.layout.alarm_overlay, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP

        windowManager?.addView(overlayView, params)

        overlayView?.findViewById<Button>(R.id.dismiss_button)?.setOnClickListener {
            stopSelf()

            windowManager?.removeView(overlayView)

            notificationManager?.cancel(1)
        }

        overlayView?.findViewById<TextView>(R.id.alarm_message)?.text = "Weather Alert!"
        overlayView?.findViewById<TextView>(R.id.des)?.text = "Weather is fine today"
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
