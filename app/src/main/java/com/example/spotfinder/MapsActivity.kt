package com.example.spotfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotfinder.databinding.ActivityMapsBinding
import com.example.spotfinder.model.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Activity for displaying locations on Google Maps
 * Can display a single selected location or multiple locations
 * Provides navigation option for single locations
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap
    private var selectedLocation: Location? = null
    private var locationsList: List<Location>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get data from intent
        selectedLocation = intent.getParcelableExtra("selected_location")
        locationsList = intent.getParcelableArrayListExtra("locations")

        // Setup navigation button
        setupNavigationButton()

        // Initialize map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Sets up the navigation button visibility and click listener
     * Only visible when a single location is selected
     */
    private fun setupNavigationButton() {
        if (selectedLocation != null) {
            // Show navigation button for single location
            binding.fabStartNavigation.show()
            binding.fabStartNavigation.setOnClickListener {
                startNavigation()
            }
        } else {
            // Hide navigation button for multiple locations
            binding.fabStartNavigation.hide()
        }
    }

    /**
     * Opens Google Maps app with navigation to the selected location
     * If Google Maps is not installed, opens in browser
     */
    private fun startNavigation() {
        selectedLocation?.let { location ->
            // Create URI for Google Maps navigation
            val uri = Uri.parse(
                "google.navigation:q=${location.latitude},${location.longitude}&mode=d"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }

            // Check if Google Maps is installed
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Fallback to browser if Google Maps is not installed
                val browserUri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}"
                )
                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                startActivity(browserIntent)
            }
        } ?: run {
            Toast.makeText(
                this,
                "No location selected for navigation",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Called when the map is ready to be used
     * Adds markers and positions camera appropriately
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configure map UI settings
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = false
        }

        // Display locations based on what was passed
        when {
            selectedLocation != null -> {
                // Display single selected location
                displaySingleLocation(selectedLocation!!)
            }
            !locationsList.isNullOrEmpty() -> {
                // Display multiple locations
                displayMultipleLocations(locationsList!!)
            }
            else -> {
                // Default to Toronto if no locations provided
                val toronto = LatLng(43.6532, -79.3832)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 10f))
            }
        }
    }

    /**
     * Displays a single location on the map with a marker
     * @param location The location to display
     */
    private fun displaySingleLocation(location: Location) {
        val position = LatLng(location.latitude, location.longitude)

        // Add marker
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(location.address)
                .snippet(location.getCoordinatesString())
        )

        // Move camera to location with zoom
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))

        // Update title
        supportActionBar?.title = getString(R.string.map_title_single, location.address)
    }

    /**
     * Displays multiple locations on the map with markers
     * Adjusts camera to show all locations
     * @param locations List of locations to display
     */
    private fun displayMultipleLocations(locations: List<Location>) {
        if (locations.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()

        // Add marker for each location
        locations.forEach { location ->
            val position = LatLng(location.latitude, location.longitude)

            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(location.address)
                    .snippet(location.getCoordinatesString())
            )

            boundsBuilder.include(position)
        }

        // Adjust camera to show all markers
        try {
            val bounds = boundsBuilder.build()
            val padding = 100 // pixels
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        } catch (e: IllegalStateException) {
            // If only one location, center on it
            if (locations.size == 1) {
                val position = LatLng(locations[0].latitude, locations[0].longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f))
            }
        }

        // Update title
        supportActionBar?.title = getString(R.string.map_title_multiple, locations.size)
    }

    /**
     * Handle back button press
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}