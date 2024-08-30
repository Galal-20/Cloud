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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Retrieve the latitude and longitude passed from MainActivity
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Add a marker at the specified location and move the camera
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title("Marker at Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f)) // Zoom level can be adjusted
    }
}
