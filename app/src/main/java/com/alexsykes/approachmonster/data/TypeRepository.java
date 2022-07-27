package com.alexsykes.approachmonster.data;

import android.app.Application;

import java.util.List;

public class TypeRepository {
    private TypeDao typeDao;
    private  List<Type> allTypes;

    public TypeRepository(Application application) {
        ApproachDatabase db = ApproachDatabase.getDatabase(application);

        typeDao = db.typeDao();

        allTypes = typeDao.getAllTypes();
    }

    public List<Type> getAllTypes() { return allTypes; }
}
