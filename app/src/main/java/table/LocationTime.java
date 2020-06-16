package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location_time_table")
public class LocationTime {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private double latitude;
    private double longitude;

    public LocationTime(String time, double latitude, double longitude) {
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
