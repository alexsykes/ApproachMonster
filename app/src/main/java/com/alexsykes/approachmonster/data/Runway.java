package com.alexsykes.approachmonster.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "runways")
public class Runway {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo
    private int runway_id;

    private double lat, lng;
    private int length;
    private int direction;
    private int elevation;
    private String airfield;
    private String ident;

    public Runway(double lat, double lng, int length, int direction, int elevation, String airfield, String ident) {
        this.lat = lat;
        this.lng = lng;
        this.length = length;
        this.direction = direction;
        this.airfield = airfield;
        this.ident = ident;
        this.elevation = elevation;
    }

    public int getRunway_id() {
        return runway_id;
    }

    public void setRunway_id(int runway_id) {
        this.runway_id = runway_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getAirfield() {
        return airfield;
    }

    public void setAirfield(String airfield) {
        this.airfield = airfield;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }
}
