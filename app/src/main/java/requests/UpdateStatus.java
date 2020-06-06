package requests;

public class UpdateStatus {
    private String macAddress;
    private String status;

    public UpdateStatus(String macAddress, String status) {
        this.macAddress = macAddress;
        this.status = status;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
