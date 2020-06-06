package requests;

public class GetMeeting {
    private String time;
    private String latitude;
    private String longitude;
    private String status;

    public GetMeeting(String time, String latitude, String longitude, String status) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
