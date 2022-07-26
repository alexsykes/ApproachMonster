package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class NavaidViewModel extends AndroidViewModel {
    private NavaidRepository navaidRepository;

    private final List<Navaid> allAirfields, allVors, allVrps;
    public NavaidViewModel(@NonNull Application application) {
        super(application);
        navaidRepository = new NavaidRepository(application);
        allAirfields = navaidRepository.getAllAirfields();
        allVors = navaidRepository.getAllVors();
        allVrps = navaidRepository.getAllVrps();
    }

    public List<Navaid> getAllAirfields() {
        return allAirfields;
    }

    public List<Navaid> getAllVors() {
        return allVors;
    }

    public List<Navaid> getAllVrps() {
        return allVrps;
    }

    public Navaid getNavaidById(int id) { return navaidRepository.getNavaidById(id); }
}
