package com.alexsykes.approachmonster.data;

import android.app.Application;

import java.util.List;

public class RunwayRepository {
    private RunwayDao runwayDao;
    private Runway runway;
    private List<Runway> runwayList;
    private int runway_id;

    public RunwayRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);
        runwayDao = db.runwayDao();
        this.runway = getRunwayById(runway_id);
        this.runwayList = getRunwayList();
    }

    void insertRunway(Runway runway) { runwayDao.insertRunway(runway); }

    public List<Runway> getRunwayList() {
        return runwayDao.getAllRunways();
    }

    public Runway getRunwayById(int runway_id) { return runwayDao.getRunwayById(runway_id); }
}
