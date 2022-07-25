package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class NavaidViewModel extends AndroidViewModel {
    private NavaidRepository navaidRepsitory;
    public NavaidViewModel(@NonNull Application application) {
        super(application);
    }
}
