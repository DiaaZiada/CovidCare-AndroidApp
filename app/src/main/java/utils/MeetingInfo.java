package utils;

public class MeetingInfo {
    private String time;
    private String status;
    private double latitude;
    private double logitude;

    public MeetingInfo(String time, String status, double latitude, double logitude) {
        this.time = time;
        this.status = status;
        this.latitude = latitude;
        this.logitude = logitude;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLogitude() {
        return logitude;
    }
}


