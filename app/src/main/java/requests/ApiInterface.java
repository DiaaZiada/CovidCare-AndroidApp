package requests;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("requestid")
    public Call<RequestId> requestId(@Body RequestId requestId);

    @POST("updatestatus")
    public Call<UpdateStatus> updateStatus(@Body UpdateStatus updateStatus);

    @POST("sendlocationtime")
    public Call<SendLocationTime> sendLocationTime(@Body SendLocationTime sendLocationTime);

    @POST("getmeetings")
    public Call<List<GetMeeting>> getMeetings(@Body RequestId requestId);



}



