package com.example.covidcare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meeting_table")
public class Meeting {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String status;
    private String macAddress;
    private String time;
    private String location;

    public Meeting(String status, String macAddress, String time, String location) {
        this.status = status;
        this.macAddress = macAddress;
        this.time = time;
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }



}
