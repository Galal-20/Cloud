package com.example.cloud.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.cloud.R
import com.example.cloud.database.AppDatabase
import com.example.cloud.ui.notification.alarm.AlarmReceiver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

@Suppress("DEPRECATION")
class SettingsBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var radioCelsius: RadioButton
    private lateinit var radioFahrenheit: RadioButton
    private lateinit var radioKelvin: RadioButton
    private lateinit var radioMeterSecond: RadioButton
    private lateinit var radioMilesHour: RadioButton
    private lateinit var radioArabic: RadioButton
    private lateinit var radioEnglish: RadioButton
    private lateinit var radioOpenNotification: RadioButton
    private lateinit var radioCloseNotification: RadioButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_settings, container, false)

        initViews(view)

        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        loadSavedPreferences()
        setupListeners()

        return view
    }

    // Initialize all views
    private fun initViews(view: View) {
        radioArabic = view.findViewById(R.id.radioArabic)
        radioEnglish = view.findViewById(R.id.radioEnglish)
        radioCelsius = view.findViewById(R.id.radioCelsius)
        radioFahrenheit = view.findViewById(R.id.radioFahrenheit)
        radioKelvin = view.findViewById(R.id.radioKelvin)
        radioMeterSecond = view.findViewById(R.id.radioMeterSecond)
        radioMilesHour = view.findViewById(R.id.radioMilesHour)
        radioOpenNotification = view.findViewById(R.id.open_notification)
        radioCloseNotification = view.findViewById(R.id.close_notification)
    }

    private fun loadSavedPreferences() {
        val savedLanguage = sharedPreferences.getString("language", "en")
        when (savedLanguage) {
            "ar" -> radioArabic.isChecked = true
            else -> radioEnglish.isChecked = true
        }

        val savedUnit = sharedPreferences.getString("temperature_unit", "Celsius")
        when (savedUnit) {
            "Celsius" -> radioCelsius.isChecked = true
            "Fahrenheit" -> radioFahrenheit.isChecked = true
            "Kelvin" -> radioKelvin.isChecked = true
        }

        val savedSpeedUnit = sharedPreferences.getString("wind_speed_unit", "Meter/Second")
        when (savedSpeedUnit) {
            "Meter/Second" -> radioMeterSecond.isChecked = true
            "Miles/Hour" -> radioMilesHour.isChecked = true
        }

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        if (notificationsEnabled) {
            radioOpenNotification.isChecked = true
        } else {
            radioCloseNotification.isChecked = true
        }

    }

    private fun setupListeners() {
        radioArabic.setOnClickListener { setLocale("ar") }
        radioEnglish.setOnClickListener { setLocale("en") }

        radioCelsius.setOnClickListener { savePreference("temperature_unit", "Celsius") }
        radioFahrenheit.setOnClickListener { savePreference("temperature_unit", "Fahrenheit") }
        radioKelvin.setOnClickListener { savePreference("temperature_unit", "Kelvin") }

        radioMeterSecond.setOnClickListener { savePreference("wind_speed_unit", "Meter/Second") }
        radioMilesHour.setOnClickListener { savePreference("wind_speed_unit", "Miles/Hour") }

        radioOpenNotification.setOnClickListener { handleNotificationPreference(true) }
        radioCloseNotification.setOnClickListener { handleNotificationPreference(false) }

    }

    private fun setLocale(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        dismiss()
        requireActivity().recreate()
    }

    private fun savePreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
        dismiss()
        requireActivity().recreate()
    }

    private fun handleNotificationPreference(enable: Boolean) {
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
                } else {
                    //showSnackbar(R.string.already.toString())
                }
            } else {
                //showSnackbar(R.string.version.toString())
            }
        } else {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivity(intent)
        }
        sharedPreferences.edit().putBoolean("notifications_enabled", enable).apply()
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showSnackbar(R.string.granted.toString())
            } else {
                //showSnackbar(R.string.denied.toString())
            }
        }
    }
}
