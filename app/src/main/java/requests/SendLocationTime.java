package requests;

public class SendLocationTime {
    private String appId;
    private String time;
    private double latitude;
    private double longitude;

    public SendLocationTime(String appId, String time, double latitude, double longitude) {
        this.appId = appId;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
