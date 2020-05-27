package com.example.covidcare;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_table")
public class Device {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String macAddress;
    private int order;

    public Device(String name, String macAddress, int order) {
        this.name = name;
        this.macAddress = macAddress;
        this.order = order;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getOrder() {
        return order;
    }
}
