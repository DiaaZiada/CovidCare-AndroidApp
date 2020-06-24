package requests;

public class RequestId {
    private String appId;

    public RequestId(String userId) {
        this.appId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
