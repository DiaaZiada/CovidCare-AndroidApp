package requests;

import android.util.Log;

import com.example.covidcare.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import table.AppInfo;
import table.LocationTime;
import table.Meeting;
import table.User;

public class RequestsModel {
    private static final String TAG = "RequestsModel";
    private static final String BASE_URL = "http://192.168.1.109:5000/";
    public static boolean getMeetingsFinished;
    public static boolean sendLocationTimeFinished;

    private ApiInterface apiInterface;
    private static RequestsModel instance;
    private static Repository repository = Repository.getInstance();

    private RequestsModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
        getMeetingsFinished = true;
        sendLocationTimeFinished = true;
    }

    public static RequestsModel getInstance() {
        if (instance == null)
            instance = new RequestsModel();
        return instance;
    }

    public void requestId(AppInfo appInfo) {
        if (appInfo.getAppId() != -1)
            return;
        RequestId requestId = new RequestId(-1);
        Call<RequestId> call = apiInterface.requestId(requestId);
        call.enqueue(new Callback<RequestId>() {
            @Override
            public void onResponse(Call<RequestId> call, Response<RequestId> response) {
                appInfo.setAppId(response.body().getAppId());
                repository.appInfoUpdate(appInfo);
            }

            @Override
            public void onFailure(Call<RequestId> call, Throwable t) {

            }
        });
    }

    public void updateStatus(AppInfo appInfo) {
        UpdateStatus updateStatus = new UpdateStatus(appInfo.getAppId(), appInfo.getStatus());
        Call<UpdateStatus> call = apiInterface.updateStatus(updateStatus);
        call.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {

            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {

            }
        });
    }


    public void sendLocationTime(AppInfo appInfo, List<LocationTime> locationTimes) {
        if (appInfo.getAppId() == -1) {
            requestId(appInfo);
            return;
        }

        sendLocationTimeFinished = false;
        for (LocationTime locationTime : locationTimes) {
            SendLocationTime sendLocationTime = new SendLocationTime(appInfo.getAppId(), locationTime.getTime(), locationTime.getLatitude(), locationTime.getLongitude());
            Call<SendLocationTime> call = apiInterface.sendLocationTime(sendLocationTime);
            call.enqueue(new Callback<SendLocationTime>() {
                @Override
                public void onResponse(Call<SendLocationTime> call, Response<SendLocationTime> response) {
                    repository.locationTimeDelete(locationTime);
                }

                @Override
                public void onFailure(Call<SendLocationTime> call, Throwable t) {

                }
            });
        }
        sendLocationTimeFinished = true;
    }


    public void getMeetings(AppInfo appInfo) {
        if (appInfo.getAppId() == -1) {
            requestId(appInfo);
            return;
        }

        RequestId requestId = new RequestId(appInfo.getAppId());

        Call<List<GetMeeting>> call = apiInterface.getMeetings(requestId);
        call.enqueue(new Callback<List<GetMeeting>>() {
            @Override
            public void onResponse(Call<List<GetMeeting>> call, Response<List<GetMeeting>> response) {
                getMeetingsFinished = false;
                for (GetMeeting getMeeting : response.body()) {
                    Meeting meeting = new Meeting(getMeeting.getStatus(), getMeeting.getTime(),
                            Double.valueOf(getMeeting.getLatitude()), Double.valueOf(getMeeting.getLongitude()));
                    repository.meetingInsert(meeting);
                }
                getMeetingsFinished = true;
            }

            @Override
            public void onFailure(Call<List<GetMeeting>> call, Throwable t) {

            }
        });
    }
}
