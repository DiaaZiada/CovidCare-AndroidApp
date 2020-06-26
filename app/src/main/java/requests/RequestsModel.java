package requests;

import android.util.Log;

import com.example.covidcare.Repository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import table.LocationTime;
import table.Meeting;
import utils.SharedVars;

public class RequestsModel {
    private static final String TAG = "RequestsModel";
    private static final String BASE_URL = "http://192.168.1.109:5000/";
    private static RequestsModel instance;
    private static Repository repository = Repository.getInstance();
    private ApiInterface apiInterface;

    private RequestsModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        SharedVars.getMeetingsFinished = false;
        SharedVars.sendLocationTimeFinished = true;

    }

    public static RequestsModel getInstance() {
        if (instance == null)
            instance = new RequestsModel();
        return instance;
    }

    public void requestId() {
        RequestId requestId = new RequestId(SharedVars.appInfo.getAppId());
        Call<RequestId> call = apiInterface.requestId(requestId);
        call.enqueue(new Callback<RequestId>() {
            @Override
            public void onResponse(Call<RequestId> call, Response<RequestId> response) {

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

        if (SharedVars.appInfo.getAppId().equals("-1"))
            return;

        UpdateStatus updateStatus = new UpdateStatus(SharedVars.appInfo.getAppId(), SharedVars.appInfo.getStatus());
        Call<UpdateStatus> call = apiInterface.updateStatus(updateStatus);
        call.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {


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

        if (SharedVars.appInfo.getAppId().equals("-1") || locationTimes.size() == 0)
            return;
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
                repository.deleteAllLocationsTimes();
            }

            @Override
            public void onFailure(Call<RequestId> call, Throwable t) {

            }
        });

    }


    public void getMeetings() {
        if (SharedVars.appInfo.getAppId().equals("-1")) {
            return;
        }

        RequestId requestId = new RequestId(SharedVars.appInfo.getAppId());

        Call<List<GetMeeting>> call = apiInterface.getMeetings(requestId);
        call.enqueue(new Callback<List<GetMeeting>>() {
            @Override
            public void onResponse(Call<List<GetMeeting>> call, Response<List<GetMeeting>> response) {
                SharedVars.getMeetingsFinished = false;
                for (GetMeeting getMeeting : response.body()) {
                    String status = getMeeting.getStatus();
                    String id = getMeeting.getId();
                    String hash = getMeeting.getHash();
                    String[] locationTime = hash.split(",");
                    Meeting meeting = new Meeting(id, status, dash2SlashSDate(locationTime[2]), Double.valueOf(locationTime[0]), Double.valueOf(locationTime[1]));
                    repository.meetingInsert(meeting);

                }
                SharedVars.getMeetingsFinished = true;
                Meeting fake = new Meeting("-1", "", "", .1, .1);
                repository.meetingInsert(fake);

            }

            @Override
            public void onFailure(Call<List<GetMeeting>> call, Throwable t) {
                SharedVars.getMeetingsFinished = true;

                Meeting fake = new Meeting("-1", "", "", .1, .1);
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
