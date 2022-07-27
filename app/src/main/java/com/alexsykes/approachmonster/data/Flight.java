package com.alexsykes.approachmonster.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="flights")
public class Flight {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name="flight")
    private String flight_id;

    private double lat, lng;
    private int altitude, vector, velocity;
    private String destination;

    private String type;

    private boolean active = true;
    private boolean expired = false;

    public Flight(@NonNull String flight_id, double lat, double lng, int altitude, int vector, int velocity, String destination, String type) {
        this.flight_id = flight_id;
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;
        this.vector = vector;
        this.velocity = velocity;
        this.destination = destination;
        this.type = type;
    }

    @NonNull
    public String getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(@NonNull String flight_id) {
        this.flight_id = flight_id;
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

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getVector() {
        return vector;
    }

    public void setVector(int vector) {
        this.vector = vector;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
