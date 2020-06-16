package requests;

public class UpdateStatus {
   private int appId;
   private String status;

    public UpdateStatus(int userId, String status) {
        this.appId = userId;
        this.status = status;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
