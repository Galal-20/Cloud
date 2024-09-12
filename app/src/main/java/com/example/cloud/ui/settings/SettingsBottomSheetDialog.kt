package com.example.cloud.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.cloud.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_settings, container, false)

        radioArabic = view.findViewById(R.id.radioArabic)
        radioEnglish = view.findViewById(R.id.radioEnglish)
        radioCelsius = view.findViewById(R.id.radioCelsius)
        radioFahrenheit = view.findViewById(R.id.radioFahrenheit)
        radioKelvin = view.findViewById(R.id.radioKelvin)
        radioMeterSecond = view.findViewById(R.id.radioMeterSecond)
        radioMilesHour = view.findViewById(R.id.radioMilesHour)
        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        save()
        setupListeners()
        return view
    }

    private fun save(){
        val savedLanguage = sharedPreferences.getString("language", "en")
        if (savedLanguage == "ar") {
            radioArabic.isChecked = true
        } else {
            radioEnglish.isChecked = true
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

    }

    private fun setupListeners() {
        radioArabic.setOnClickListener {
            setLocale("ar")
            dismiss()
        }
        radioEnglish.setOnClickListener {
            setLocale("en")
            dismiss()
        }
        radioCelsius.setOnClickListener {
            saveTemperatureUnit("Celsius")
            dismiss()
        }
        radioFahrenheit.setOnClickListener {
            saveTemperatureUnit("Fahrenheit")
            dismiss()
        }
        radioKelvin.setOnClickListener {
            saveTemperatureUnit("Kelvin")
            dismiss()
        }

        radioMeterSecond.setOnClickListener { saveUnitPreference("wind_speed_unit", "Meter/Second") }
        radioMilesHour.setOnClickListener { saveUnitPreference("wind_speed_unit", "Miles/Hour") }
    }

    private fun setLocale(languageCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )

        requireActivity().recreate()
    }

    private fun saveTemperatureUnit(unit: String) {
        val editor = sharedPreferences.edit()
        editor.putString("temperature_unit", unit)
        editor.apply()
        requireActivity().recreate()
    }

    private fun saveUnitPreference(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
        dismiss()
        requireActivity().recreate()
    }
}
