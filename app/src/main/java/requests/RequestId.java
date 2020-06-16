package requests;

public class RequestId {
    private int appId;

    public RequestId(int userId) {
        this.appId = userId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
}
