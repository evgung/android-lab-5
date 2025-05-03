package com.example.laba5.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.laba5.data.entities.Airport
import com.example.laba5.data.entities.Favourite

@Dao
interface FlightDao {
    @Query("SELECT * FROM airport " +
            "WHERE name LIKE '%' || :query || '%' " +
            "OR iata_code LIKE '%' || :query || '%'" +
            "ORDER BY passengers DESC")
    suspend fun searchAirports(query: String): List<Airport>

    @Query("SELECT * FROM airport " +
            "WHERE id != :airportId " +
            "ORDER BY passengers DESC")
    suspend fun getFlightsFrom(airportId: Int): List<Airport>

    @Insert
    suspend fun saveFavourite(favourite: Favourite)

    @Delete
    suspend fun deleteFavourite(favourite: Favourite)

    @Query("SELECT * FROM favourite")
    suspend fun getFavourites(): List<Favourite>

    @Query("SELECT COUNT(*) > 0 FROM favourite " +
            "WHERE departure_code = :depCode " +
            "AND destination_code = :destCode")
    suspend fun isFavourite(depCode: String, destCode: String): Boolean

    @Query("SELECT * FROM favourite " +
            "WHERE departure_code = :depCode " +
            "AND destination_code = :destCode " +
            "LIMIT 1")
    suspend fun getFavourite(depCode: String, destCode: String): Favourite

    @Query("DELETE FROM favourite")
    suspend fun clearAllFavorites()
}