package com.alexsykes.approachmonster.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class TypeViewModel extends AndroidViewModel {
    private TypeRepository typeRepository;
    private final List<Type> allTypes;

    public TypeViewModel(@NonNull Application application) {
        super(application);

        allTypes = typeRepository.getAllTypes();
    }
    public List<Type> getAllTypes() { return allTypes; }
}
