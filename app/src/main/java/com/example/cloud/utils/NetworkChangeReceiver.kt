package com.example.cloud.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

class NetworkChangeReceiver(private val listener: NetworkChangeListener?) : BroadcastReceiver() {

    companion object {
        private const val TAG = "NetworkChangeReceiver"

        fun isConnectedToInternet(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                    if (capabilities != null) {
                        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    }
                } else {
                    val activeNetwork = cm.activeNetworkInfo
                    return activeNetwork != null && activeNetwork.isConnected
                }
            }
            return false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = isConnectedToInternet(context)
        Log.d(TAG, "Network connectivity change detected, isConnected: $isConnected")
        listener?.onNetworkChange(isConnected)
    }

    interface NetworkChangeListener {
        fun onNetworkChange(isConnected: Boolean)
    }
}
