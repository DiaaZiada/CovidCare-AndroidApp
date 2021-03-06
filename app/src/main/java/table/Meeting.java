package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meeting_table")
public class Meeting {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String app_id;
    private String status;
    private String time;
    private double latitude;
    private double longitude;

    public Meeting(String app_id, String status, String time, double latitude, double longitude) {
        this.app_id = app_id;
        this.status = status;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getStatus() {
        return status;
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
