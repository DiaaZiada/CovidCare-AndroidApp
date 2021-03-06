package table;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_info_table")

public class AppInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String appId;
    private String status;

    public AppInfo(String appId, String status) {
        this.appId = appId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
