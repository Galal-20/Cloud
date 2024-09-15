package com.example.cloud.ui.splash

import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.cloud.R
import com.example.cloud.ui.main.MainActivity
import com.example.cloud.utils.network.NetworkChangeReceiver
import com.example.cloud.databinding.ActivitySplashBinding
import com.example.cloud.utils.network.Check_Network
import mumayank.com.airlocationlibrary.AirLocation
import java.util.Locale

@Suppress("Don't worry about this", "DEPRECATION")
class Splash : AppCompatActivity(), AirLocation.Callback, NetworkChangeReceiver.NetworkChangeListener {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private lateinit var airLocation: AirLocation
    private var city: String = ""
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var dataFetched = false

    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private lateinit var checkNetwork: Check_Network

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkNetwork = Check_Network(this, binding.root)
        checkNetworkStatus()
        yoyo()
    }

    private fun yoyo() {
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(binding.imageSplash)
        YoYo.with(Techniques.FadeIn).duration(3000).playOn(binding.textSplash)
    }

    fun getLocation() {
        airLocation = AirLocation(
            this,
            this,
            false,
            3,
            R.string.location_text.toString()
        )
        airLocation.start()
    }

    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
        Toast.makeText(this, R.string.check_location, Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(locations: ArrayList<Location>) {
        if (dataFetched) {
            Log.d("Splash", R.string.data_fetch.toString())
        } else {
            lat = locations[0].latitude
            lon = locations[0].longitude
            val geocoder = Geocoder(this, Locale.getDefault())

            try {
                val addressList = geocoder.getFromLocation(lat, lon, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    city = "${address.adminArea}, ${address.countryName}"
                    dataFetched = true
                    navigateToMainActivity()
                } else {
                    // Address list is empty or null
                    Toast.makeText(this, R.string.Invalid, Toast.LENGTH_SHORT).show()
                    binding.textSplash.text = getString(R.string.Location_Unknown)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //Toast.makeText(this, R.string.geocoder_service_not_available, Toast.LENGTH_SHORT).show()
                binding.textSplash.text = getString(R.string.Location_Unknown)
            }
        }
    }


    private fun navigateToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            it.putExtra("latitude", lat)
            it.putExtra("longitude", lon)
            startActivity(it)
            finish()
        }
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
        checkNetwork.onNetworkChange(isConnected)
        if (isConnected) {
            if (!dataFetched) {
                getLocation()
            }
        } else {
            navigateToMainActivity()
        }
    }

    private fun checkNetworkStatus() {
        if (checkNetwork.isConnectedToInternet(this)) {
            getLocation()
            checkNetwork.onNetworkChange(true)
        } else {
            checkNetwork.onNetworkChange(false)
            navigateToMainActivity()
        }
    }

    // *******************************************************************************************
   /* private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }*/
   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now show notifications
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }*/


}
