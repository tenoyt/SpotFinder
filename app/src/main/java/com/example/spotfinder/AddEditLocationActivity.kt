package com.example.spotfinder

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotfinder.database.LocationDatabaseHelper
import com.example.spotfinder.databinding.ActivityAddEditLocationBinding
import com.example.spotfinder.model.Location

/**
 * Activity for adding new locations or editing existing ones
 * Provides form validation and database operations
 */
class AddEditLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLocationBinding
    private lateinit var databaseHelper: LocationDatabaseHelper
    private var existingLocation: Location? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper
        databaseHelper = LocationDatabaseHelper(this)

        // Check if we're in edit mode
        checkMode()

        // Setup button listeners
        setupListeners()
    }

    /**
     * Checks if the activity is in edit mode and loads existing location data
     */
    private fun checkMode() {
        val mode = intent.getStringExtra("mode")
        existingLocation = intent.getParcelableExtra("location")

        if (mode == "edit" && existingLocation != null) {
            isEditMode = true
            setupEditMode()
        } else {
            setupAddMode()
        }
    }

    /**
     * Configures the UI for add mode
     */
    private fun setupAddMode() {
        supportActionBar?.title = getString(R.string.title_add_location)
        binding.buttonSave.text = getString(R.string.button_add)
    }

    /**
     * Configures the UI for edit mode and populates fields with existing data
     */
    private fun setupEditMode() {
        supportActionBar?.title = getString(R.string.title_edit_location)
        binding.buttonSave.text = getString(R.string.button_update)

        existingLocation?.let { location ->
            binding.editTextAddress.setText(location.address)
            binding.editTextLatitude.setText(location.latitude.toString())
            binding.editTextLongitude.setText(location.longitude.toString())
        }
    }

    /**
     * Sets up click listeners for buttons
     */
    private fun setupListeners() {
        // Save button - validates input and saves to database
        binding.buttonSave.setOnClickListener {
            if (validateInput()) {
                if (isEditMode) {
                    updateLocation()
                } else {
                    addLocation()
                }
            }
        }

        // Cancel button - closes the activity
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    /**
     * Validates user input for all fields
     * @return true if all inputs are valid, false otherwise
     */
    private fun validateInput(): Boolean {
        val address = binding.editTextAddress.text.toString().trim()
        val latitudeStr = binding.editTextLatitude.text.toString().trim()
        val longitudeStr = binding.editTextLongitude.text.toString().trim()

        // Validate address
        if (address.isEmpty()) {
            binding.editTextAddress.error = getString(R.string.error_address_required)
            binding.editTextAddress.requestFocus()
            return false
        }

        // Validate latitude
        if (latitudeStr.isEmpty()) {
            binding.editTextLatitude.error = getString(R.string.error_latitude_required)
            binding.editTextLatitude.requestFocus()
            return false
        }

        val latitude = try {
            latitudeStr.toDouble()
        } catch (e: NumberFormatException) {
            binding.editTextLatitude.error = getString(R.string.error_latitude_invalid)
            binding.editTextLatitude.requestFocus()
            return false
        }

        if (latitude < -90.0 || latitude > 90.0) {
            binding.editTextLatitude.error = getString(R.string.error_latitude_range)
            binding.editTextLatitude.requestFocus()
            return false
        }

        // Validate longitude
        if (longitudeStr.isEmpty()) {
            binding.editTextLongitude.error = getString(R.string.error_longitude_required)
            binding.editTextLongitude.requestFocus()
            return false
        }

        val longitude = try {
            longitudeStr.toDouble()
        } catch (e: NumberFormatException) {
            binding.editTextLongitude.error = getString(R.string.error_longitude_invalid)
            binding.editTextLongitude.requestFocus()
            return false
        }

        if (longitude < -180.0 || longitude > 180.0) {
            binding.editTextLongitude.error = getString(R.string.error_longitude_range)
            binding.editTextLongitude.requestFocus()
            return false
        }

        return true
    }

    /**
     * Adds a new location to the database
     */
    private fun addLocation() {
        val address = binding.editTextAddress.text.toString().trim()
        val latitude = binding.editTextLatitude.text.toString().toDouble()
        val longitude = binding.editTextLongitude.text.toString().toDouble()

        val location = Location(
            id = 0, // Will be auto-generated
            address = address,
            latitude = latitude,
            longitude = longitude
        )

        val id = databaseHelper.addLocation(location)
        if (id > 0) {
            Toast.makeText(this, R.string.success_location_added, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.error_add_location, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates an existing location in the database
     */
    private fun updateLocation() {
        existingLocation?.let { existing ->
            val address = binding.editTextAddress.text.toString().trim()
            val latitude = binding.editTextLatitude.text.toString().toDouble()
            val longitude = binding.editTextLongitude.text.toString().toDouble()

            val updatedLocation = Location(
                id = existing.id,
                address = address,
                latitude = latitude,
                longitude = longitude
            )

            val rowsUpdated = databaseHelper.updateLocation(updatedLocation)
            if (rowsUpdated > 0) {
                Toast.makeText(this, R.string.success_location_updated, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, R.string.error_update_location, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Enable back button in action bar
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}