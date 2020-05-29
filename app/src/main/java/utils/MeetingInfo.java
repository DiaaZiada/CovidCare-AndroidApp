package utils;

public class MeetingInfo {
    private String time;
    private String status;
    private String location;

    public MeetingInfo(String time, String status, String location) {
        this.time = time;
        this.status = status;
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
