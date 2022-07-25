package com.alexsykes.approachmonster.data;

import android.app.Application;

public class NavaidRepository {
    private NavaidDao navaidDao;

    NavaidRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);

        navaidDao = db.navaidDao();
    }
}
