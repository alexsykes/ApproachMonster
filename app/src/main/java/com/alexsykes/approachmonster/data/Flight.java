package com.alexsykes.approachmonster.data;


import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName="flights")
public class Flight {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo
    private String flight_id;

    private double lat, lng;
    private int altitude, vector, velocity;
    private int targetAltitude, targetVector, targetVelocity;


    private String destination;

    private String type;

    private boolean active = true;
    private boolean expired = false;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    long lastUpdated;

    public Flight(@NonNull String flight_id, double lat, double lng, int altitude, int vector, int velocity, String destination, String type) {
        this.flight_id = flight_id;
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;
        this.vector = vector;
        this.velocity = velocity;
        this.destination = destination;
        this.type = type;
        this.targetAltitude = altitude;
        this.targetVector = vector;
        this.targetVelocity = velocity;
        long currentTimeInMillis = System.currentTimeMillis();
        this.lastUpdated = currentTimeInMillis;
    }

    public void move(int timeLapse) {
        LatLng current = new LatLng(lat, lng);
        float displacement = (float) (velocity * (0.51444 * timeLapse /1000));
        LatLng newLatLng = SphericalUtil.computeOffset(current,displacement, vector);
        this.setLat(newLatLng.latitude);
        this.setLng(newLatLng.longitude);
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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getTargetAltitude() {
        return targetAltitude;
    }

    public void setTargetAltitude(int targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    public int getTargetVector() {
        return targetVector;
    }

    public void setTargetVector(int targetVector) {
        this.targetVector = targetVector;
    }

    public int getTargetVelocity() {
        return targetVelocity;
    }

    public void setTargetVelocity(int targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    public ClimbData getClimbData(Flight flight)  {
        ClimbData climbData = new ClimbData();
        if (flight.altitude < 5000) {
            climbData.speedLimit = 290;
            climbData.climeRate = 3000;
        } else if(flight.altitude < 24000) {
            climbData.speedLimit = 460;
            climbData.climeRate = 2000;
        } else {
            climbData.speedLimit = 460;
            climbData.climeRate = 1500;
        }
        return climbData;
    }

    private class ClimbData {
        Flight flight;
        int speedLimit, climeRate;
    }
}
