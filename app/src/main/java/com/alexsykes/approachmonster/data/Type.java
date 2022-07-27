package com.alexsykes.approachmonster.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "types")
public class Type {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name="ICAO")
    private String ICAO;

    private String IATA, model;

    public Type(@NonNull String IATA, String ICAO, String model) {
        this.ICAO = ICAO;
        this.IATA = IATA;
        this.model = model;
    }

    @NonNull
    public String getICAO() {
        return ICAO;
    }


    public String getIATA() {
        return IATA;
    }

    public void setIATA(String IATA) {
        this.IATA = IATA;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
