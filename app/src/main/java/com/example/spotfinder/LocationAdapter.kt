package com.example.spotfinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView adapter for displaying a list of locations
 * Uses ListAdapter for efficient list updates with DiffUtil
 */
class LocationAdapter(
    private val onItemClick: (Location) -> Unit,
    private val onEditClick: (Location) -> Unit,
    private val onDeleteClick: (Location) -> Unit
) : ListAdapter<Location, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {

    /**
     * ViewHolder class for location items
     * Holds references to views and binds location data
     */
    inner class LocationViewHolder(
        private val binding: ItemLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a location object to the view
         */
        fun bind(location: Location) {
            binding.apply {
                textViewAddress.text = location.address
                textViewCoordinates.text = location.getCoordinatesString()

                // Set click listener for the entire item
                root.setOnClickListener {
                    onItemClick(location)
                }

                // Set click listener for edit button
                buttonEdit.setOnClickListener {
                    onEditClick(location)
                }

                // Set click listener for delete button
                buttonDelete.setOnClickListener {
                    onDeleteClick(location)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * DiffUtil callback for efficient list updates
     * Determines which items have changed in the list
     */
    private class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }
}