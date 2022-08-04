package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class RunwayViewModel extends AndroidViewModel {
    private RunwayRepository runwayRepository;
    private final Runway runway;
    private final List<Runway> runwayList;
    private int runway_id;

    public RunwayViewModel(@NonNull Application application) {
        super(application);

        runwayRepository = new RunwayRepository(application);
        runway = runwayRepository.getRunwayById(runway_id);
        runwayList = runwayRepository.getRunwayList();
    }

    public Runway getRunwayById(int runway_id) { return runway; };

    public List<Runway> getRunwayList() {
        return runwayList;
    }
}
