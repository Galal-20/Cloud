package com.example.cloud.ui.map

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud.R
import com.example.cloud.ui.main.view.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation: LatLng
    private var currentMarker: Marker? = null

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
                Toast.makeText(this, R.string.select_location, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val location = LatLng(latitude, longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        currentMarker = mMap.addMarker(MarkerOptions().position(location).title(location.toString()))

        mMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng

            currentMarker?.remove()
            currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(location.toString()))
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



