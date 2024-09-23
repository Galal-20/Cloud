package com.example.cloud.ui.settings

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
import androidx.core.app.NotificationManagerCompat
import com.example.cloud.R
import com.example.cloud.databinding.BottomSheetSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

@Suppress("DEPRECATION")
class SettingsBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
//        dialog?.window?.setBackgroundDrawableResource(R.drawable.backgroundshapeweather)


        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        loadSavedPreferences()
        setupListeners()

        return binding.root
    }

    // Clean up binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSavedPreferences() {
        val savedLanguage = sharedPreferences.getString("language", "en")
        when (savedLanguage) {
            "ar" -> binding.radioArabic.isChecked = true
            else -> binding.radioEnglish.isChecked = true
        }

        val savedUnit = sharedPreferences.getString("temperature_unit", "Celsius")
        when (savedUnit) {
            "Celsius" -> binding.radioCelsius.isChecked = true
            "Fahrenheit" -> binding.radioFahrenheit.isChecked = true
            "Kelvin" -> binding.radioKelvin.isChecked = true
        }

        val savedSpeedUnit = sharedPreferences.getString("wind_speed_unit", "Meter/Second")
        when (savedSpeedUnit) {
            "Meter/Second" -> binding.radioMeterSecond.isChecked = true
            "Miles/Hour" -> binding.radioMilesHour.isChecked = true
        }

        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        if (notificationsEnabled) {
            binding.openNotification.isChecked = true
        } else {
            binding.closeNotification.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.radioArabic.setOnClickListener { setLocale("ar") }
        binding.radioEnglish.setOnClickListener { setLocale("en") }

        binding.radioCelsius.setOnClickListener { savePreference("temperature_unit", "Celsius") }
        binding.radioFahrenheit.setOnClickListener { savePreference("temperature_unit", "Fahrenheit") }
        binding.radioKelvin.setOnClickListener { savePreference("temperature_unit", "Kelvin") }

        binding.radioMeterSecond.setOnClickListener { savePreference("wind_speed_unit", "Meter/Second") }
        binding.radioMilesHour.setOnClickListener { savePreference("wind_speed_unit", "Miles/Hour") }

        binding.openNotification.setOnClickListener { handleNotificationPreference(true) }
        binding.closeNotification.setOnClickListener { handleNotificationPreference(false) }
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
                }
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
