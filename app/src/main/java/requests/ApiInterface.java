package requests;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("requestid")
    Call<RequestId> requestId(@Body RequestId requestId);

    @POST("updatestatus")
    Call<UpdateStatus> updateStatus(@Body UpdateStatus updateStatus);

    @POST("sendlocationtime")
    Call<RequestId> sendLocationTime(@Body ArrayList<SendLocationTime> sendLocationTime);

    @POST("getmeetings")
    Call<List<GetMeeting>> getMeetings(@Body RequestId requestId);


}



