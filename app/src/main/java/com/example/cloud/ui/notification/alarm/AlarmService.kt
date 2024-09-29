package com.example.cloud.ui.notification.alarm

import android.app.NotificationManager
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
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope

import com.example.cloud.R
import com.example.cloud.model.Daily
import com.example.cloud.repository.remote.WeatherRepositoryImpl
import com.example.cloud.retrofit.ApiState
import com.galal.weather.ViewModel.WeatherViewModel
import kotlinx.coroutines.launch


class AlarmService : LifecycleService() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var mediaPlayer: MediaPlayer? = null
    private var notificationManager: NotificationManager? = null
    private lateinit var weatherViewModel: WeatherViewModel


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        mediaPlayer = MediaPlayer.create(this, R.raw.mixkit_classical_vibes)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        weatherViewModel = WeatherViewModel(WeatherRepositoryImpl(this))

        val lat = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
        val lon = intent?.getDoubleExtra("lon", 0.0) ?: 0.0

        fetchWeather(lat, lon)

        showAlarmOverlay()

        return START_STICKY
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        lifecycleScope.launch {
            weatherViewModel.fetchCurrentWeatherDataByCoordinates(lat, lon)
            weatherViewModel.weatherDataStateFlow.collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        val currentTemp = apiState.data
                        updateUi(currentTemp as Daily)
                    }
                    is ApiState.Failure -> {}
                    is ApiState.Loading -> {}
                }
            }
        }
    }
    private fun updateUi(weatherData: Daily){
        overlayView?.findViewById<TextView>(R.id.des)?.
        text = "The weather today is ${weatherData.weather.firstOrNull()?.description}"

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
        overlayView?.findViewById<TextView>(R.id.des)?.text = "Don't forget your weather"
    }


    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        notificationManager?.cancel(1)

    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}


