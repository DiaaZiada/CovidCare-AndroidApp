package requests;

import android.util.Log;

import com.example.covidcare.MainActivity;
import com.example.covidcare.Repository;
import utils.SharedVars;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import table.LocationTime;
import table.Meeting;

public class RequestsModel {
    private static final String TAG = "RequestsModel";
    private static final String BASE_URL = "http://192.168.1.108:5000/";
    private ApiInterface apiInterface;
    private static RequestsModel instance;
    private static Repository repository = Repository.getInstance();

    private RequestsModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        SharedVars.getMeetingsFinished = false;
        SharedVars.sendLocationTimeFinished = true;
//        getedId = false;
//        counter = 0;
    }

    public static RequestsModel getInstance() {
        if (instance == null)
            instance = new RequestsModel();
        return instance;
    }

    public void requestId() {
        Log.e(TAG, "requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId ");
        RequestId requestId = new RequestId(SharedVars.appInfo.getAppId());
        Call<RequestId> call = apiInterface.requestId(requestId);
//        counter++;
        call.enqueue(new Callback<RequestId>() {
            @Override
            public void onResponse(Call<RequestId> call, Response<RequestId> response) {
                Log.i(TAG, "requestId" + "\t" + response.body().getAppId() + "\t" + SharedVars.appInfo.getAppId() + "\t" + requestId.getAppId());
//                getedId = true;
//                counter++;
                SharedVars.appInfo.setAppId(response.body().getAppId());
                repository.appInfoUpdate(SharedVars.appInfo);
            }

            @Override
            public void onFailure(Call<RequestId> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void updateStatus() {
        Log.e(TAG, "updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus ");

        if (SharedVars.appInfo.getAppId().equals("-1"))
            return;
//        counter++;
        UpdateStatus updateStatus = new UpdateStatus(SharedVars.appInfo.getAppId(), SharedVars.appInfo.getStatus());
        Call<UpdateStatus> call = apiInterface.updateStatus(updateStatus);
        call.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {

                Log.i(TAG, "updateStatus" + "\t" + response.body().getAppId() + "\t" + SharedVars.appInfo.getAppId() + "\t" + updateStatus.getAppId());

                if (response.body().getAppId().equals(SharedVars.appInfo.getAppId()))
                    return;
                SharedVars.appInfo.setAppId(response.body().getAppId());
                repository.appInfoUpdate(SharedVars.appInfo);

            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {
                Log.e(TAG, t.getMessage());

            }
        });
    }


    public void sendLocationTime(List<LocationTime> locationTimes) {
        if (SharedVars.appInfo.getAppId().equals("-1") || locationTimes.size()==0)
            return;
        Log.e(TAG, "sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime ");
        SharedVars.sendLocationTimeFinished = false;
        ArrayList<SendLocationTime> sendLocationTimes = new ArrayList<>();
        for (LocationTime locationTime : locationTimes) {
            SendLocationTime sendLocationTime = new SendLocationTime(SharedVars.appInfo.getAppId(), locationTime.getTime(), locationTime.getLatitude(), locationTime.getLongitude());
            sendLocationTimes.add(sendLocationTime);
        }
        Call<RequestId> call = apiInterface.sendLocationTime(sendLocationTimes);
        call.enqueue(new Callback<RequestId>() {
            @Override
            public void onResponse(Call<RequestId> call, Response<RequestId> response) {
                Log.i(TAG, response.body().getAppId());
                repository.deleteAllLocationsTimes();
            }

            @Override
            public void onFailure(Call<RequestId> call, Throwable t) {

            }
        });
        SharedVars.sendLocationTimeFinished =false;

}


    public void getMeetings() {
        if (SharedVars.appInfo.getAppId().equals("-1")) {
            Log.e(TAG, "NOT ID NOT ID NOT ID NOT ID NOT ID NOT ID NOT ID NOT ID NOT ID NOT ID ");
            return;
        }
//        counter++;
        Log.e(TAG, "getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings ");

        RequestId requestId = new RequestId(SharedVars.appInfo.getAppId());

        Call<List<GetMeeting>> call = apiInterface.getMeetings(requestId);
        call.enqueue(new Callback<List<GetMeeting>>() {
            @Override
            public void onResponse(Call<List<GetMeeting>> call, Response<List<GetMeeting>> response) {
                Log.i(TAG, "getMeetings" + "\t" + SharedVars.appInfo.getAppId() + "\t" + requestId.getAppId());
                SharedVars.getMeetingsFinished = false;
                for (GetMeeting getMeeting : response.body()) {
//                    aaa++;
                    String status = getMeeting.getStatus();
                    String id = getMeeting.getId();
                    String hash = getMeeting.getHash();
                    String[] locationTime = hash.split(",");
                    Meeting meeting = new Meeting(id, status, dash2SlashSDate(locationTime[2]), Double.valueOf(locationTime[0]), Double.valueOf(locationTime[1]));
                    repository.meetingInsert(meeting);

                }
                SharedVars.getMeetingsFinished = true;
                Meeting fake = new Meeting("-1", "", "",.1,.1);
                repository.meetingInsert(fake);

            }

            @Override
            public void onFailure(Call<List<GetMeeting>> call, Throwable t) {
                SharedVars.getMeetingsFinished = true;

                Meeting fake = new Meeting("-1", "", "",.1,.1);
                repository.meetingInsert(fake);

            }
        });
    }

    private String dash2SlashSDate(String date) {
        String[] times = date.split("-");
        date = times[0] + "/" + times[1] + "/" + times[2] + " " + times[3] + ":" + times[4];
        return date;
    }
}
