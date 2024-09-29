/*
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
import android.location.Geocoder
import android.widget.EditText
import java.util.Locale


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation: LatLng
    private var currentMarker: Marker? = null

    private lateinit var searchView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        searchView = findViewById(R.id.SearchView)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView.setOnEditorActionListener { _, _, _ ->
            val locationName = searchView.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
            true
        }

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

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    currentMarker?.remove()
                    currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))

                    selectedLocation = latLng
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

*/
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
import android.location.Geocoder
import android.widget.EditText
import java.util.Locale


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation: LatLng
    private var currentMarker: Marker? = null

    private lateinit var searchView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        searchView = findViewById(R.id.SearchView)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView.setOnEditorActionListener { _, _, _ ->
            val locationName = searchView.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
            true
        }

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

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    currentMarker?.remove()
                    currentMarker = mMap.addMarker(MarkerOptions().position(latLng).title(address.getAddressLine(0)))

                    selectedLocation = latLng
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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


