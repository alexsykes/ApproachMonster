package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FlightRepository {
    private FlightDao flightDao;
    private List<Flight> activeFlights;
    private LiveData<List<Flight>> pendingFlights;
    private LiveData<List<Flight>> expiredFlights;

    FlightRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);

        flightDao = db.flightDao();

        activeFlights = flightDao.getActiveFlights();
        pendingFlights = flightDao.getPendingFlights();
        expiredFlights = flightDao.getExpiredFlights();
    }

    void insertFlight(Flight flight){ flightDao.insertFlight(flight); }

    public List<Flight> getActiveFlights() {
        return activeFlights;
    }

    public LiveData<List<Flight>> getExpiredFlights() {
        return expiredFlights;
    }

    public LiveData<List<Flight>> getPendingFlights() {
        return pendingFlights;
    }
}
