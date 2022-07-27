package com.alexsykes.approachmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFlight(Flight flight);

    @Query("DELETE FROM flights")
    void deleteAllFlights();

    @Query("SELECT * FROM flights WHERE active")
    List<Flight> getActiveFlights();

    @Query("SELECT * FROM flights WHERE NOT active AND NOT expired")
    LiveData<List<Flight>> getPendingFlights();


    @Query("SELECT * FROM flights WHERE NOT active AND expired")
    LiveData<List<Flight>> getExpiredFlights();
}
