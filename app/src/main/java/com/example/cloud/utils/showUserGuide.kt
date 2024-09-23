package com.example.cloud.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.TextView
import com.example.cloud.R
import com.example.cloud.databinding.ActivityMainBinding
import com.example.cloud.ui.favourites.view.FavoritesBottomSheetDialog
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView

object showUserGuide {
    @SuppressLint("ResourceType")
    fun showUserGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.favImage,
                activity.getString(R.string.add_to_favorites_title),
                activity.getString(R.string.add_to_favorites_desc)
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
                binding.notificationImage,
                activity.getString(R.string.notifications_title),
                activity.getString(R.string.notifications_desc)
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
                binding.settings,
                activity.getString(R.string.settings_title),
                activity.getString(R.string.settings_desc)
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
                binding.openFav,
                activity.getString(R.string.open_favorites_title),
                activity.getString(R.string.open_favorites_desc)
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showLocationGuide(activity, binding)
                }
            })
    }

    private fun showLocationGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.textLocation,
                "Location","This is your current location\nYou can set location from map using " +
                        "pressed on the location text and mark your location from map"
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object :TapTargetView.Listener(){
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    showHourlyForecastGuide(activity, binding)
                }
            }
        )
    }

    private fun showHourlyForecastGuide(activity: Activity, binding: ActivityMainBinding) {
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                binding.hoursRecyclerView,
                activity.getString(R.string.hourly_forecast_title),
                activity.getString(R.string.hourly_forecast_desc)
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
                binding.dayRecyclerView,
                activity.getString(R.string.daily_forecast_title),
                activity.getString(R.string.daily_forecast_desc)
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

    fun showSwipeGuide(bottomSheetDialog: FavoritesBottomSheetDialog, titleView: TextView) {
        TapTargetView.showFor(
            bottomSheetDialog.requireActivity(),
            TapTarget.forView(
                titleView,
                bottomSheetDialog.getString(R.string.swipe_guide_title),
                bottomSheetDialog.getString(R.string.swipe_guide_desc)
            )
                .outerCircleColor(R.color.gray)
                .targetCircleColor(R.color.white)
                .textColor(android.R.color.black)
                .cancelable(false)
                .transparentTarget(true),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                }
            }
        )
    }




}
