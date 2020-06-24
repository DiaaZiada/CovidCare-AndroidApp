package requests;

import android.util.Log;

import com.example.covidcare.MainActivity;
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

public class RequestsModel {
    private static final String TAG = "RequestsModel";
    private static final String BASE_URL = "http://192.168.1.109:5000/";
    public static boolean getMeetingsFinished;
    public static boolean sendLocationTimeFinished;
    public static boolean getedId;
    public static int counter;

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
        getedId = false;
        counter = 0;
    }

    public static RequestsModel getInstance() {
        if (instance == null)
            instance = new RequestsModel();
        return instance;
    }

    public void requestId() {
        Log.e(TAG, "requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId requestId ");
        RequestId requestId = new RequestId(MainActivity.appInfo.getAppId());
        Call<RequestId> call = apiInterface.requestId(requestId);
        counter++;
        call.enqueue(new Callback<RequestId>() {
            @Override
            public void onResponse(Call<RequestId> call, Response<RequestId> response) {
                Log.i(TAG, "requestId" + "\t" + response.body().getAppId() + "\t" + MainActivity.appInfo.getAppId() + "\t" + requestId.getAppId());
                getedId = true;
                counter++;
                MainActivity.appInfo.setAppId(response.body().getAppId());
                repository.appInfoUpdate(MainActivity.appInfo);
            }

            @Override
            public void onFailure(Call<RequestId> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void updateStatus() {
        Log.e(TAG, "updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus updateStatus ");

        if (!getedId)
            return;
        UpdateStatus updateStatus = new UpdateStatus(MainActivity.appInfo.getAppId(), MainActivity.appInfo.getStatus());
        Call<UpdateStatus> call = apiInterface.updateStatus(updateStatus);
        call.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {

                Log.i(TAG, "updateStatus" + "\t" + response.body().getAppId() + "\t" + MainActivity.appInfo.getAppId() + "\t" + updateStatus.getAppId());

                if (response.body().getAppId() == MainActivity.appInfo.getAppId())
                    return;
                MainActivity.appInfo.setAppId(response.body().getAppId());
                repository.appInfoUpdate(MainActivity.appInfo);

            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {
                Log.e(TAG, t.getMessage());

            }
        });
    }


    public void sendLocationTime(List<LocationTime> locationTimes) {
        if (!getedId)
            return;
        Log.e(TAG, "sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime sendLocationTime ");
        sendLocationTimeFinished = false;
        for (LocationTime locationTime : locationTimes) {
            SendLocationTime sendLocationTime = new SendLocationTime(MainActivity.appInfo.getAppId(), locationTime.getTime(), locationTime.getLatitude(), locationTime.getLongitude());
            Call<RequestId> call = apiInterface.sendLocationTime(sendLocationTime);
            call.enqueue(new Callback<RequestId>() {
                @Override
                public void onResponse(Call<RequestId> call, Response<RequestId> response) {
                    Log.i(TAG, "sendLocationTime" + "\t" + response.body().getAppId() + "\t" + MainActivity.appInfo.getAppId() + "\t" + sendLocationTime.getAppId());

                    sendLocationTimeFinished = false;

                    repository.locationTimeDelete(locationTime);

                    if (locationTime.hashCode() == locationTimes.get(locationTimes.size() - 1).hashCode())
                        sendLocationTimeFinished = true;

                    if (response.body().getAppId() == MainActivity.appInfo.getAppId())
                        return;

                    MainActivity.appInfo.setAppId(response.body().getAppId());
                    repository.appInfoUpdate(MainActivity.appInfo);


                }

                @Override
                public void onFailure(Call<RequestId> call, Throwable t) {
                    sendLocationTimeFinished = true;
                    Log.e(TAG, t.getMessage());


                }
            });

        }
        sendLocationTimeFinished = true;

    }


    public void getMeetings() {
        if (!getedId)
            return;

        Log.e(TAG, "getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings getMeetings ");


        RequestId requestId = new RequestId(MainActivity.appInfo.getAppId());

        Call<List<GetMeeting>> call = apiInterface.getMeetings(requestId);
        call.enqueue(new Callback<List<GetMeeting>>() {
            @Override
            public void onResponse(Call<List<GetMeeting>> call, Response<List<GetMeeting>> response) {
                Log.i(TAG, "getMeetings" + "\t" + MainActivity.appInfo.getAppId() + "\t" + requestId.getAppId());
                getMeetingsFinished = false;
                repository.deleteAllMeetings();
                for (GetMeeting getMeeting : response.body()) {
                    String status = getMeeting.getStatus();
                    String id = getMeeting.getId();
                    String hash = getMeeting.getHash();
                    String[] locationTime = hash.split(",");
                    Meeting meeting = new Meeting(id, status, dash2SlashSDate(locationTime[2]), Double.valueOf(locationTime[0]), Double.valueOf(locationTime[1]));

                    if (getMeeting.hashCode() == response.body().get(response.body().size() - 1).hashCode())
                        getMeetingsFinished = true;

                    repository.meetingInsert(meeting);
                }
            }

            @Override
            public void onFailure(Call<List<GetMeeting>> call, Throwable t) {
                getMeetingsFinished = true;
            }
        });
    }
    private String dash2SlashSDate(String date){
        String[] times = date.split("-");
        date = times[0]+"/"+times[1]+"/"+times[2] +" "+ times[3]+":"+times[4];
        return date;
    }
}
