package com.example.laba5.ui.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laba5.R
import com.example.laba5.data.dao.FlightDao
import com.example.laba5.data.database.FlightsDatabase
import com.example.laba5.data.repositories.PreferencesRepository
import com.example.laba5.databinding.ActivityMainBinding
import com.example.laba5.ui.adapters.AirportAdapter
import com.example.laba5.ui.adapters.FavouriteAdapter
import com.example.laba5.ui.adapters.FlightAdapter
import com.example.laba5.ui.viewmodels.FlightViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FlightsDatabase
    private lateinit var flightDao: FlightDao
    private lateinit var preferencesRepository: PreferencesRepository
    private val viewModel: FlightViewModel by viewModels {
        FlightViewModel.getFactory(flightDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FlightsDatabase.getInstance(this)
        flightDao = database.flightDao()
        preferencesRepository = PreferencesRepository(dataStore)

        setupObservers()
        setupSearch()
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            preferencesRepository.saveSearchQuery(binding.searchInput.text.toString())
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    showFavoritesList()
                } else {
                    viewModel.searchAirports(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lifecycleScope.launch {
            preferencesRepository.searchQuery.collect {
                val savedQuery = preferencesRepository.searchQuery.first()
                binding.searchInput.setText(savedQuery)
            }
        }
    }

    private fun setupObservers() {
        viewModel.airports.observe(this) { airports ->
            if (airports.isNotEmpty()) {
                showAirportsList()
                binding.airportsList.adapter = AirportAdapter(
                    airports
                ) {
                    viewModel.selectAirport(it)
                    //showFlightsList()
                }
                binding.airportsList.layoutManager = LinearLayoutManager(this)
            } else {
                binding.airportsList.visibility = View.GONE
            }
        }

        viewModel.flights.observe(this) { flights ->
            if (flights.isNotEmpty()) {
                showFlightsList()
                val departure = viewModel.selectedAirport.value ?: return@observe
                binding.flightsList.adapter = FlightAdapter(
                    departure,
                    flights,
                    { dep, dest -> viewModel.isFavourite(dep, dest) },
                    { dep, dest -> viewModel.toggleFavorite(dep, dest) }
                )
                binding.flightsList.layoutManager = LinearLayoutManager(this)
            } else {
                binding.flightsList.visibility = View.GONE
            }
        }

        viewModel.favorites.observe(this) { favorites ->
            binding.favoritesList.adapter = FavouriteAdapter(favorites) { favorite ->
                viewModel.deleteFavorite(favorite)
            }
            binding.favoritesList.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun showAirportsList() {
        binding.airportsList.visibility = View.VISIBLE
        binding.flightsList.visibility = View.GONE
        binding.favoritesList.visibility = View.GONE
    }

    private fun showFlightsList() {
        binding.airportsList.visibility = View.GONE
        binding.flightsList.visibility = View.VISIBLE
        binding.favoritesList.visibility = View.GONE
    }

    private fun showFavoritesList() {
        binding.airportsList.visibility = View.GONE
        binding.flightsList.visibility = View.GONE
        binding.favoritesList.visibility = View.VISIBLE
    }
}