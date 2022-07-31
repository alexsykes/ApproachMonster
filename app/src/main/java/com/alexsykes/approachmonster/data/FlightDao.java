package com.alexsykes.approachmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFlight(Flight flight);

    @Query("DELETE FROM flights")
    void deleteAllFlights();

    @Query("UPDATE flights SET lat = :lat, lng = :lng WHERE flight_id = :flight_id")
    void updatePosition(double lat, double lng, String flight_id);

    @Query("SELECT * FROM flights WHERE active")
    List<Flight> getActiveFlightList();

    @Query("SELECT * FROM flights WHERE NOT active AND NOT expired")
    LiveData<List<Flight>> getPendingFlights();


    @Query("SELECT * FROM flights WHERE NOT active AND expired")
    LiveData<List<Flight>> getExpiredFlights();

    @Query("SELECT * FROM flights WHERE flight_id = :flight_id")
    Flight getFlight(String flight_id);
}
