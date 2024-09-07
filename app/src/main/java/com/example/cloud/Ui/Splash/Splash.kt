package com.example.cloud.Ui.Splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.cloud.R
import com.example.cloud.Ui.Main.MainActivity
import com.example.cloud.Utils.NetworkChangeReceiver
import com.example.cloud.Utils.NetworkChangeReceiver.Companion.isConnectedToInternet
import com.example.cloud.databinding.ActivitySplashBinding
import com.google.android.material.snackbar.Snackbar
import mumayank.com.airlocationlibrary.AirLocation
import java.util.Locale

class Splash : AppCompatActivity(), AirLocation.Callback ,NetworkChangeReceiver.NetworkChangeListener{
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private lateinit var airLocation: AirLocation
    private var city: String = ""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var dataFetched = false

    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private var wasConnected = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkNetworkStatus()
        yoyo()

    }

    private fun yoyo() {
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(binding.imageSplash)
        YoYo.with(Techniques.FadeIn).duration(3000).playOn(binding.textSplash)
        //getLocation()
    }

    private fun getLocation() {
        airLocation = AirLocation(this,
            this,
            false,
            3,
            "Please enable location permissions from settings to proceed")
        airLocation.start()
    }

    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
        Toast.makeText(this, "Check your location permission", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(locations: ArrayList<Location>) {
        if (dataFetched) {
            Toast.makeText(this, "Data already fetched", Toast.LENGTH_SHORT).show()
        }else {

            locations[0].accuracy
            lat = locations[0].latitude
            lon = locations[0].longitude
            val geocoder = Geocoder(this, Locale.getDefault())

            val address = geocoder.getFromLocation(lat, lon, 1)
            if (address != null) {
                city = address[0].adminArea + ", " + address[0].countryName
                dataFetched = true
                navigateToMainActivity()
            } else {
                binding.textSplash.text = "Location Unknown"
            }
        }
    }

    private fun navigateToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            it.putExtra("latitude", lat)
            it.putExtra("longitude", lon)
            it.putExtra("city", city)
            startActivity(it)
            finish()
        }
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        networkChangeReceiver = NetworkChangeReceiver(this)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver?.let {
            unregisterReceiver(it)
        }
    }

    override fun onNetworkChange(isConnected: Boolean) {
        if (isConnected){
            if (wasConnected){
                showCustomSnackbar("Back Online", Snackbar.LENGTH_SHORT)
                wasConnected = false
                getLocation()
            }
        }else{
            showCustomSnackbar("Internet is not available", Snackbar.LENGTH_INDEFINITE)
            wasConnected = true
        }
    }
    private fun checkNetworkStatus() {
        if (isConnectedToInternet(this)) {
            getLocation()
            showCustomSnackbar("Connected to the Internet", Snackbar.LENGTH_SHORT)
        } else {
            showCustomSnackbar("No Internet connection", Snackbar.LENGTH_INDEFINITE)
        }
    }


    private fun isConnectedToInternet(context: Context): Boolean {
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

    @SuppressLint("RestrictedApi")
    private fun showCustomSnackbar(message: String, duration: Int) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "", duration)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
