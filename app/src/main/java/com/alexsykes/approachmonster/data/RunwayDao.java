package com.alexsykes.approachmonster.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RunwayDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRunway(Runway runway);

    @Query("DELETE FROM runways")
    void deleteAllRunways();

    @Query("SELECT * FROM runways WHERE runway_id = :runway_id")
    Runway getRunwayById(int runway_id);

    @Query("SELECT * FROM runways ORDER BY airfield, ident")
    List<Runway> getAllRunways();
}
