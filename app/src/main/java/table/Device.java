package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_table")
public class Device {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String macAddress;
    private String time;
    private double latitude;
    private double longitude;

    public Device(String name, String macAddress, String time, double latitude, double longitude) {
        this.name = name;
        this.macAddress = macAddress;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
