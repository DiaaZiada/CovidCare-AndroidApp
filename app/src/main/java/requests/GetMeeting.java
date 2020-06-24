package requests;

public class GetMeeting {
    private String status;
    private String id;
    private String hash;


    public GetMeeting(String status, String id, String hash) {
        this.status = status;
        this.id = id;
        this.hash = hash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
