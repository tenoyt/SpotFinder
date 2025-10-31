package com.example.spotfinder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a geographic location
 * Implements Parcelable for easy passing between Android components
 *
 * @property id Unique identifier for the location (auto-generated in database)
 * @property address Human-readable address string
 * @property latitude Geographic latitude coordinate
 * @property longitude Geographic longitude coordinate
 */
@Parcelize
data class Location(
    val id: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {

    /**
     * Returns a formatted string representation of the coordinates
     */
    fun getCoordinatesString(): String {
        return "Lat: %.4f, Lng: %.4f".format(latitude, longitude)
    }

    /**
     * Checks if the location has valid coordinates
     */
    fun hasValidCoordinates(): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
}