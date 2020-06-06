package requests;

public class AddMeeting {
    private String macAddress_user1;
    private String macAddress_user2;
    private String latitude;
    private String longitude;
    private String time;

    public AddMeeting(String macAddress_user1, String macAddress_user2, String latitude, String longitude, String time) {
        this.macAddress_user1 = macAddress_user1;
        this.macAddress_user2 = macAddress_user2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getMacAddress_user1() {
        return macAddress_user1;
    }

    public void setMacAddress_user1(String macAddress_user1) {
        this.macAddress_user1 = macAddress_user1;
    }

    public String getMacAddress_user2() {
        return macAddress_user2;
    }

    public void setMacAddress_user2(String macAddress_user2) {
        this.macAddress_user2 = macAddress_user2;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
