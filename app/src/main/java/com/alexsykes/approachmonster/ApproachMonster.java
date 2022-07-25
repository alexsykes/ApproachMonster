package com.alexsykes.approachmonster;

import android.app.Application;
import android.util.Log;

import com.alexsykes.approachmonster.data.ApproachDatabase;

public class ApproachMonster extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Info", "onCreateLaunch: ");
        Log.i("Info", "onCreateLaunch: done ");
    }
}
