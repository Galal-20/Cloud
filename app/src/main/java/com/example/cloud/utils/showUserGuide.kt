package com.example.cloud.utils

import android.annotation.SuppressLint
import android.app.Activity
import com.example.cloud.R
import com.example.cloud.databinding.ActivityMainBinding
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView

object showUserGuide {
    @SuppressLint("ResourceType")
    fun showUserGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.favImage, "Add to favorites",
                "Save your favorite weather."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showNotificationGuide(activity, binding)
                }
            })
    }

    private fun showNotificationGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.notificationImage, "Notifications",
                "View recent notifications."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showSettingsGuide(activity, binding)
                }
            })
    }

    private fun showSettingsGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.settings, "Settings",
                "Convert temperature units, wind speed units, and change language."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showFavoritesGuide(activity, binding)
                }
            })
    }

    private fun showFavoritesGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.openFav, "Open weather Favourites",
                "Show your saved favorite weather."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showHourlyForecastGuide(activity, binding)
                }
            })
    }
    private fun showHourlyForecastGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.hoursRecyclerView, "Hourly Forecast",
                "Show hourly weather forecast."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.white)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showDailyForecastGuide(activity, binding)
                }
            })
    }
    private fun showDailyForecastGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.dayRecyclerView, "Daily Forecast",
                "Show Daily weather forecast."
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.white)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                }
            })
    }

}