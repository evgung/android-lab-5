package com.example.laba5.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laba5.data.entities.Airport
import com.example.laba5.databinding.ItemAirportBinding

class AirportAdapter(
    private val airports: List<Airport>,
    private val onItemClick: (Airport) -> Unit
) : RecyclerView.Adapter<AirportAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAirportBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAirportBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val airport = airports[position]

        with (holder.binding) {
            airportName.text = airport.name
            airportCode.text = airport.iataCode

            root.setOnClickListener {
                onItemClick(airport)
            }
        }
    }
    
    override fun getItemCount() = airports.size
}