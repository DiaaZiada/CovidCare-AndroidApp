package requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {


    @POST("addmeeting")
    public Call<RequestStatus> addMeeting(@Body AddMeeting addMeeting);

    @POST("getmeetings")
    public Call<List<GetMeeting>> getMeetings(@Body MacAddress macAddress);

    @POST("updatestatus")
    public Call<RequestStatus> updateStatus(@Body UpdateStatus updateStatus);

    @POST("requestid")
    public Call<RequestId> requestId(@Body RequestId requestId);






}



