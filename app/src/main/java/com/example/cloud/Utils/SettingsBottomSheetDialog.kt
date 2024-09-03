package com.example.cloud.Utils

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

   /* private lateinit var radioCelsius: RadioButton
    private lateinit var radioFahrenheit: RadioButton
    private lateinit var radioKelvin: RadioButton
    private lateinit var radioMeterSecond: RadioButton
    private lateinit var radioMilesHour: RadioButton*/
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

        sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        val savedLanguage = sharedPreferences.getString("language", "en")
        if (savedLanguage == "ar") {
            radioArabic.isChecked = true
        } else {
            radioEnglish.isChecked = true
        }

        setupListeners()

        return view
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
}
