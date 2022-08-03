package com.alexsykes.approachmonster.data;

import android.app.Application;

import java.util.List;

public class RunwayRepository {
    private RunwayDao runwayDao;
    private Runway runway;
    private List<Runway> runwayList;

    public RunwayRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);
        runwayDao = db.runwayDao();

        this.runwayDao = runwayDao;
        this.runway = runway;
        this.runwayList = runwayList;
    }

    void insertRunway(Runway runway) { runwayDao.insertRunway(runway); }

    public List<Runway> getRunwayList() {
        return runwayDao.getAllRunways();
    }

    public Runway getRunway(int runway_id) { return runwayDao.getRunwayById(runway_id); }
}
