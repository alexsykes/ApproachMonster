package com.alexsykes.approachmonster.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="navaids")
public class Navaid {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "navaidID")
    private int navaid_id;

    public int getNavaid_id() {
        return navaid_id;
    }

    private double lat, lng;
    private String name;
    private String code;
    private String type;

    public Navaid(int navaid_id, String code, String name, String type, double lat, double lng) {
        this.navaid_id = navaid_id;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.code = code;
        this.type = type;
    }

    public void setNavaid_id(int navaid_id) {
        this.navaid_id = navaid_id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
