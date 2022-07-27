package com.alexsykes.approachmonster.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TypeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Type type);

    @Query ("SELECT * FROM types ORDER by ICAO")
    List<Type> getAllTypes();

    @Query("DELETE FROM types")
    void deleteAllTypes();

}
