package requests;

public class UpdateStatus {
    private String appId;
    private String status;

    public UpdateStatus(String userId, String status) {
        this.appId = userId;
        this.status = status;
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
