/*
package com.example.cloud.Ui.Main

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud.R
import com.google.android.gms.maps.GoogleMap

class MapActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

       // webView = findViewById(R.id.webView)

        // Enable JavaScript for WebView
        webView.settings.javaScriptEnabled = true

        // Set WebViewClient to open links in the WebView
        webView.webViewClient = WebViewClient()

        // Retrieve the latitude and longitude passed from MainActivity
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Load Google Maps URL with the location
        val url = "https://www.google.com/maps?q=$latitude,$longitude"
        webView.loadUrl(url)
    }
}
*/

package com.example.cloud.Ui.Map

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud.R
import com.example.cloud.Ui.Main.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation: LatLng // Store the newly selected location
    private var currentMarker: Marker? = null // Keep a reference to the current marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnChangeLocation).setOnClickListener {
            if (::selectedLocation.isInitialized) {
                fetchWeatherForLocation(selectedLocation.latitude, selectedLocation.longitude)
            } else {
                Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val location = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        currentMarker = mMap.addMarker(MarkerOptions().position(location).title("Marker at Location"))

        mMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng

            currentMarker?.remove()
            currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title("New Location"))
        }
    }

    private fun fetchWeatherForLocation(latitude: Double, longitude: Double) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
        finish()
    }
}



