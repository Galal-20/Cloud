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
import com.example.cloud.utils.PreferencesUtils
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


        sharedPreferences = PreferencesUtils.getPreferences(requireContext())

        loadSavedPreferences()
        setupListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSavedPreferences() {
        val savedLanguage = PreferencesUtils.getLanguage(requireContext())
        when (savedLanguage) {
            "ar" -> binding.radioArabic.isChecked = true
            else -> binding.radioEnglish.isChecked = true
        }

        val savedUnit = PreferencesUtils.getTemperatureUnit(requireContext())
        when (savedUnit) {
            "Celsius" -> binding.radioCelsius.isChecked = true
            "Fahrenheit" -> binding.radioFahrenheit.isChecked = true
            "Kelvin" -> binding.radioKelvin.isChecked = true
        }

        val savedSpeedUnit = PreferencesUtils.getWindSpeedUnit(requireContext())
        when (savedSpeedUnit) {
            "Meter/Second" -> binding.radioMeterSecond.isChecked = true
            "Miles/Hour" -> binding.radioMilesHour.isChecked = true
        }

        val notificationsEnabled =PreferencesUtils.isNotificationsEnabled(requireContext())
        if (notificationsEnabled) {
            binding.openNotification.isChecked = true
        } else {
            binding.closeNotification.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.radioArabic.setOnClickListener { PreferencesUtils.setLanguage(requireContext(), "ar"); setLocale("ar") }
        binding.radioEnglish.setOnClickListener { PreferencesUtils.setLanguage(requireContext(), "en"); setLocale("en") }


        binding.radioCelsius.setOnClickListener {
            savePreference(
                "${PreferencesUtils.setTemperatureUnit(requireContext(), "Celsius")}",
                "Celsius"
            )
        }
        binding.radioFahrenheit.setOnClickListener {
            savePreference(
                "${PreferencesUtils.setTemperatureUnit(requireContext(), "Fahrenheit")}",
                "Fahrenheit"
            )
        }
        binding.radioKelvin.setOnClickListener {
            savePreference(
                "${PreferencesUtils.setTemperatureUnit(requireContext(), "Kelvin")}",
                "Kelvin"
            )
        }


        binding.radioMeterSecond.setOnClickListener {
            savePreference("${PreferencesUtils.setWindSpeedUnit(requireContext(), "Meter/Second")}", "Meter/Second")
        }
        binding.radioMilesHour.setOnClickListener {
            savePreference("${PreferencesUtils.setWindSpeedUnit(requireContext(), "Miles/Hour")}", "Miles/Hour")
        }

        binding.openNotification.setOnClickListener {
            PreferencesUtils.setNotificationsEnabled(requireContext(), true); handleNotificationPreference(true);dismiss()
        }
        binding.closeNotification.setOnClickListener {
            PreferencesUtils.setNotificationsEnabled(requireContext(), false);handleNotificationPreference(false); dismiss()
        }
    }

    private fun setLocale(languageCode: String) {
        PreferencesUtils.setLanguage(requireContext(), languageCode).also {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = requireContext().resources.configuration
            config.setLocale(locale)
            requireContext().resources.updateConfiguration(
                config,
                requireContext().resources.displayMetrics
            )
            dismiss()
            requireActivity().recreate()
        }
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
        PreferencesUtils.setNotificationsEnabled(requireContext(), enable)
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


