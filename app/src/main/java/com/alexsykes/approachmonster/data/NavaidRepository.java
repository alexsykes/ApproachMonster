package com.alexsykes.approachmonster.data;

import android.app.Application;

import java.util.List;

public class NavaidRepository {
    private NavaidDao navaidDao;
    private List<Navaid> vorList;
    private List<Navaid> airfieldList;
    private List<Navaid> vrpList;
    private List<Navaid> allWaypoints;

    NavaidRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);

        navaidDao = db.navaidDao();
        airfieldList = navaidDao.getAllAirfields();
        vorList = navaidDao.getAllVors();
        vrpList = navaidDao.getAllVrps();
        allWaypoints = navaidDao.getAllWaypoints();
    }

    void insertNavaid(Navaid navaid) {
        navaidDao.insertNavaid(navaid);
    }
    List<Navaid> getAllAirfields() {
        return airfieldList;
    };

    public List<Navaid> getAllVors() {
        return vorList;
    }

    public List<Navaid> getAllVrps() {
        return vrpList;
    }

    public Navaid getNavaidById(int id) { return navaidDao.getMarkerById(id);    }

    public List<Navaid> getAllWaypoints() { return allWaypoints;     }
}
