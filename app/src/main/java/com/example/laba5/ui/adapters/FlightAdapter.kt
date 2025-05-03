package com.example.laba5.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.laba5.R
import com.example.laba5.data.entities.Airport
import com.example.laba5.data.entities.Favourite
import com.example.laba5.databinding.ItemAirportBinding
import com.example.laba5.databinding.ItemFlightBinding
import com.example.laba5.ui.adapters.AirportAdapter.ViewHolder

class FlightAdapter(
    private val departure: Airport,
    private val flights: List<Airport>,
    private val isFavorite: (String, String) -> LiveData<Boolean>,
    private val onFavoriteClick: (String, String) -> Unit
) : RecyclerView.Adapter<FlightAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFlightBinding)
        : RecyclerView.ViewHolder(binding.root) {
            var favourite = MutableLiveData<Boolean>()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFlightBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val arrive = flights[position]

        with (holder.binding) {
            departCode.text = departure.iataCode
            departName.text = departure.name
            arriveCode.text = arrive.iataCode
            arriveName.text = arrive.name
        }

        holder.favourite = isFavorite(departure.iataCode, arrive.iataCode) as MutableLiveData<Boolean>

        holder.favourite.observeForever {
            holder.binding.favoriteButton.setColorFilter(
                if (it) Color.parseColor("#d4ae33") else Color.GRAY
            )
        }

        holder.binding.favoriteButton.setOnClickListener {
            onFavoriteClick(departure.iataCode, arrive.iataCode)
            holder.favourite.value = !(holder.favourite.value)!!
        }
    }

    override fun getItemCount() = flights.size
}