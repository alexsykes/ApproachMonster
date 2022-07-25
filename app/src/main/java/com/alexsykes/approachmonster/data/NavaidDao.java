package com.alexsykes.approachmonster.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;

import java.util.List;

@Dao
public interface NavaidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNavaid(Navaid navaid);


    @Query("DELETE FROM navaids")
    void deleteAllNavaids();

    @Query("SELECT * FROM navaids ORDER BY name")
    List<Navaid> getAllNavaids();

}
