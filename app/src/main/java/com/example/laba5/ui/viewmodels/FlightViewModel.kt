package com.example.laba5.ui.viewmodels

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.laba5.data.dao.FlightDao
import com.example.laba5.data.entities.Airport
import com.example.laba5.data.entities.Favourite
import com.example.laba5.data.repositories.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlightViewModel(
    private val flightDao: FlightDao
) : ViewModel() {
    private val _selectedAirport = MutableLiveData<Airport>()
    private val _airports = MutableLiveData<List<Airport>>(emptyList())
    private val _flights = MutableLiveData<List<Airport>>(emptyList())
    private val _favorites = MutableLiveData<List<Favourite>>(emptyList())

    val selectedAirport: LiveData<Airport> = _selectedAirport
    val airports: LiveData<List<Airport>> = _airports
    val flights: LiveData<List<Airport>> = _flights
    val favorites: LiveData<List<Favourite>> = _favorites

    init {
        loadFavorites()
    }

    fun searchAirports(query: String) = viewModelScope.launch {
        _airports.value = flightDao.searchAirports(query)
    }

    fun selectAirport(airport: Airport) = viewModelScope.launch {
        _selectedAirport.value = airport
        _flights.value = flightDao.getFlightsFrom(airport.id)
    }

    fun loadFavorites() = viewModelScope.launch {
        _favorites.value = flightDao.getFavourites()
    }

    fun toggleFavorite(depCode: String, destCode: String) = viewModelScope.launch {
        if (flightDao.isFavourite(depCode, destCode)) {
            val favourite = flightDao.getFavourite(depCode, destCode)
            flightDao.deleteFavourite(favourite)
        } else {
            val favourite = Favourite(departureCode = depCode, destinationCode = destCode)
            flightDao.saveFavourite(favourite)
        }

        loadFavorites()
    }

    fun deleteFavorite(favourite: Favourite) = viewModelScope.launch {
        flightDao.deleteFavourite(favourite)
        loadFavorites()
    }

    fun isFavourite(depCode: String, destCode: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val data = flightDao.isFavourite(depCode, destCode)
            result.value = data
        }
        return result
    }

    companion object {
        fun getFactory(
            flightDao: FlightDao
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FlightViewModel(flightDao) as T
            }
        }
    }
}