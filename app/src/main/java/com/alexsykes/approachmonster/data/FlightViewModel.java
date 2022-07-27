package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class FlightViewModel extends AndroidViewModel {
    private FlightRepository flightRepository;
    private final LiveData<List<Flight>>  pendingFlights, expiredFlights;
    private final List<Flight> activeFlights;


    public FlightViewModel(@NonNull Application application) {
        super(application);

        flightRepository = new FlightRepository(application);
        activeFlights = flightRepository.getActiveFlights();
        pendingFlights = flightRepository.getPendingFlights();
        expiredFlights = flightRepository.getExpiredFlights();
    }

    public LiveData<List<Flight>> getPendingFlights() {
        return pendingFlights;
    }

    public LiveData<List<Flight>> getExpiredFlights() {
        return expiredFlights;
    }

    public List<Flight> getActiveFlights() {
        return activeFlights;
    }

    public void updateFlightPosition(double lat, double lng, String flight_id) {
        flightRepository.updateFlightPosition(lat, lng, flight_id);
    }
}
