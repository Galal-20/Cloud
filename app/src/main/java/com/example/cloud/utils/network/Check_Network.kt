package com.example.cloud.utils.network

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.cloud.R
import com.example.cloud.ui.splash.Splash
import com.google.android.material.snackbar.Snackbar

class Check_Network(private val context: Context, private val rootView: View) :
    NetworkChangeReceiver.NetworkChangeListener {

    private var wasConnected = false

    override fun onNetworkChange(isConnected: Boolean) {
        if (isConnected) {
            if (wasConnected) {
                showCustomSnackbar("Connected to the Internet", Snackbar.LENGTH_SHORT)
                val i = Intent(context, Splash::class.java)
                startActivity(context, i, null)
                if (context is Activity) {
                    context.finish()
                }
                wasConnected = false

            }
        } else {
            showCustomSnackbar("Internet is not available", Snackbar.LENGTH_SHORT)
            wasConnected = true
        }
    }



     fun isConnectedToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = it.getNetworkCapabilities(it.activeNetwork)
                capabilities?.let { cap ->
                    return cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
            } else {
                val activeNetwork = it.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isConnected
            }
        }
        return false
    }

    @SuppressLint("InflateParams", "RestrictedApi")
    private fun showCustomSnackbar(message: String, duration: Int) {
        val snackbar = Snackbar.make(rootView, "", duration)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customSnackbarView = inflater.inflate(R.layout.snackbar_custom, null)
        snackbarLayout.addView(customSnackbarView, 0)

        val textView = customSnackbarView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = message

        val iconView = customSnackbarView.findViewById<ImageView>(R.id.snackbar_icon)
        if (message.contains("Internet is not available")) {
            iconView.setImageResource(R.drawable.ic_no_internet)
        } else {
            iconView.setImageResource(R.drawable.ic_wifi)
        }

        snackbar.show()
    }


}



