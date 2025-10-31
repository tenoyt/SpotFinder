package com.example.spotfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotfinder.adapter.LocationAdapter
import com.example.spotfinder.database.LocationDatabaseHelper
import com.example.spotfinder.databinding.ActivityMainBinding
import com.example.spotfinder.model.Location

/**
 * Main activity for the SpotFinder application
 * Provides search functionality and navigation to map view and location management
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: LocationDatabaseHelper
    private lateinit var locationAdapter: LocationAdapter
    private var allLocations: List<Location> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper
        databaseHelper = LocationDatabaseHelper(this)

        // Setup RecyclerView for displaying search results
        setupRecyclerView()

        // Setup button listeners
        setupListeners()

        // Load all locations initially
        loadAllLocations()
    }

    /**
     * Configures the RecyclerView with adapter and layout manager
     */
    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter(
            onItemClick = { location ->
                // When a location is clicked, show it on the map
                showLocationOnMap(location)
            },
            onEditClick = { location ->
                // Navigate to edit screen
                navigateToEditLocation(location)
            },
            onDeleteClick = { location ->
                // Delete the location
                deleteLocation(location)
            }
        )

        binding.recyclerViewLocations.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = locationAdapter
        }
    }

    /**
     * Sets up click listeners for buttons
     */
    private fun setupListeners() {
        // Search button - searches for locations matching the query
        binding.buttonSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, R.string.error_search_empty, Toast.LENGTH_SHORT).show()
                loadAllLocations()
            } else {
                searchLocations(query)
            }
        }

        // Add location button - navigates to add location screen
        binding.buttonAddLocation.setOnClickListener {
            val intent = Intent(this, AddEditLocationActivity::class.java)
            startActivity(intent)
        }

        // View all button - shows all locations
        binding.buttonViewAll.setOnClickListener {
            loadAllLocations()
            binding.editTextSearch.text?.clear()
        }

        // View on map button - shows all locations on map
        binding.buttonViewMap.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putParcelableArrayListExtra("locations", ArrayList(allLocations))
            startActivity(intent)
        }
    }

    /**
     * Loads all locations from the database and displays them
     */
    private fun loadAllLocations() {
        allLocations = databaseHelper.getAllLocations()
        locationAdapter.submitList(allLocations)

        val count = allLocations.size
        binding.textViewResultCount.text = getString(R.string.status_showing_locations, count)
    }

    /**
     * Searches for locations matching the given query
     * @param query The search string to match against addresses
     */
    private fun searchLocations(query: String) {
        val results = databaseHelper.searchLocationsByAddress(query)
        locationAdapter.submitList(results)

        val count = results.size
        binding.textViewResultCount.text = getString(R.string.status_found_locations, count)

        if (results.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_no_results, query), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the map activity to display a specific location
     * @param location The location to display on the map
     */
    private fun showLocationOnMap(location: Location) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("selected_location", location)
        startActivity(intent)
    }

    /**
     * Navigates to the edit location screen
     * @param location The location to edit
     */
    private fun navigateToEditLocation(location: Location) {
        val intent = Intent(this, AddEditLocationActivity::class.java)
        intent.putExtra("location", location)
        intent.putExtra("mode", "edit")
        startActivity(intent)
    }

    /**
     * Deletes a location from the database
     * @param location The location to delete
     */
    private fun deleteLocation(location: Location) {
        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, location.address))
            .setPositiveButton(R.string.dialog_delete_positive) { _, _ ->
                val rowsDeleted = databaseHelper.deleteLocation(location.id)
                if (rowsDeleted > 0) {
                    Toast.makeText(this, R.string.success_location_deleted, Toast.LENGTH_SHORT).show()
                    loadAllLocations() // Refresh the list
                } else {
                    Toast.makeText(this, R.string.error_delete_location, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.dialog_delete_negative, null)
            .show()
    }

    /**
     * Refresh the location list when returning from other activities
     */
    override fun onResume() {
        super.onResume()
        loadAllLocations()
    }
}