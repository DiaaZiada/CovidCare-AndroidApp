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

    public Device(String name, String macAddress, String time) {
        this.name = name;
        this.macAddress = macAddress;
        this.time = time;
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

    public String  getTime() {
        return time;
    }
}
