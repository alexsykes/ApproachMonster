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

    @Query("UPDATE flights SET targetAltitude = :targetAltitude, targetVector = :targetVector, targetVelocity = :targetVelocity WHERE flight_id = :flight_id")
    void updateFlight(String flight_id, int targetAltitude, int targetVector, int targetVelocity);

    @Query("UPDATE flights SET altitude = :currentAlt, vector = :currentVector, velocity = :currentVelocity WHERE flight_id = :flight_id")
    void updateFlight(int currentVector, int currentVelocity, int currentAlt, String flight_id);

    @Query("UPDATE flights SET expired = 1, active = 0")
    void expireFlights();
}
