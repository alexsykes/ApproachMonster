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

    @Query("SELECT * FROM navaids WHERE type = 'Airfield' AND code NOT LIKE '---' ORDER BY name")
    List<Navaid> getAllAirfields();

    @Query("SELECT * FROM navaids WHERE type = 'VOR' OR type = 'VOR/DME' OR type = 'VORTAC' ORDER BY name")
    List<Navaid> getAllVors();

    @Query("SELECT * FROM navaids WHERE type = 'VRP' ORDER BY name")
    List<Navaid> getAllVrps();

    @Query("SELECT * FROM navaids WHERE navaidID = :id ORDER BY name")
    Navaid getMarkerById(int id);
}
