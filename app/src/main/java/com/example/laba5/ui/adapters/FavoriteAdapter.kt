package com.example.laba5.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laba5.data.entities.Favourite
import com.example.laba5.databinding.ItemFlightBinding

class FavouriteAdapter(
    private val favourites: List<Favourite>,
    private val onRemoveClick: (Favourite) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFlightBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFlightBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favourite = favourites[position]

        with (holder.binding) {
            departCode.text = favourite.departureCode
            arriveCode.text = favourite.destinationCode

            favoriteButton.setColorFilter(
                Color.parseColor("#d4ae33")
            )
            favoriteButton.setOnClickListener {
                onRemoveClick(favourite)
            }
        }
    }

    override fun getItemCount() = favourites.size
}