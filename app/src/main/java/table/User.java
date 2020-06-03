package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String status;
    private String macAddress;
    private boolean addLocation;

    public User(String name, String status, String macAddress, boolean addLocation) {
        this.name = name;
        this.status = status;
        this.macAddress = macAddress;
        this.addLocation = addLocation;
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

    public String getStatus() {
        return status;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public boolean getAddLocation() {
        return addLocation;
    }
}
