package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class FlightRepository {
    private FlightDao flightDao;
    private List<Flight> activeFlightList;
    private LiveData<List<Flight>> pendingFlights;
    private LiveData<List<Flight>> expiredFlights;

    FlightRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);

        flightDao = db.flightDao();

        activeFlightList = flightDao.getActiveFlightList();
        pendingFlights = flightDao.getPendingFlights();
        expiredFlights = flightDao.getExpiredFlights();
    }


    void insertFlight(Flight flight){ flightDao.insertFlight(flight); }

    public List<Flight> getActiveFlightList() {
        return flightDao.getActiveFlightList();
    }

    public LiveData<List<Flight>> getExpiredFlights() {
        return expiredFlights;
    }

    public LiveData<List<Flight>> getPendingFlights() {
        return pendingFlights;
    }

    public void updateFlightPosition(double lat, double lng, String flight_id) {
        flightDao.updatePosition(lat, lng, flight_id);
    }

    public void destroyFlights() {
        flightDao.deleteAllFlights();
    }

    public void expireFlights() {
        flightDao.expireFlights();
    }
}
