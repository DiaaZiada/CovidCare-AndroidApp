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
import table.Device;
import table.Meeting;
import table.User;

public class RequestsModel {
    private static final String TAG = "RequestsModel";
    private static final String BASE_URL = "http://192.168.1.106:5000/";
    private ApiInterface apiInterface;
    private static RequestsModel instance;
    private static Repository repository = Repository.getInstance();

    private RequestsModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public static RequestsModel getInstance() {
        if (instance == null)
            instance = new RequestsModel();
        return instance;
    }


    public void addMeeting(final List<Device> devices, String macAddress_user1) {
        ArrayList<AddMeeting> addMeetings = new ArrayList<AddMeeting>();
        for (Device device : devices) {
            String macAddress_user2 = device.getMacAddress();
            String latitude = String.valueOf(device.getLatitude());
            String longitude = String.valueOf(device.getLongitude());
            String time = device.getTime();
            Log.e(TAG, "Addd metting ddddddddddddddddd");

            addMeetings.add(new AddMeeting(macAddress_user1, macAddress_user2, latitude, longitude, time));
        }
        repository.deleteAllDevices();
        Log.e(TAG, "DELETE ALL DEVS");

        for (AddMeeting addMeeting : addMeetings) {
            Call<RequestStatus> call = apiInterface.addMeeting(addMeeting);

            call.enqueue(new Callback<RequestStatus>() {
                @Override
                public void onResponse(Call<RequestStatus> call, Response<RequestStatus> response) {
                }

                @Override
                public void onFailure(Call<RequestStatus> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
    }

    public void getMeetings(String macAddress_user1) {

        MacAddress macAddress = new MacAddress(macAddress_user1);

        Call<List<GetMeeting>> call = apiInterface.getMeetings(macAddress);

        call.enqueue(new Callback<List<GetMeeting>>() {
            @Override
            public void onResponse(Call<List<GetMeeting>> call, Response<List<GetMeeting>> response) {
                repository.deleteAllMeetings();
                for (GetMeeting meeting : response.body()) {
                    Meeting meet = new Meeting(meeting.getStatus(), meeting.getTime(),
                            Double.valueOf(meeting.getLatitude()), Double.valueOf(meeting.getLongitude()));
                    repository.meetingInsert(meet);
                }
            }

            @Override
            public void onFailure(Call<List<GetMeeting>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void updateStatus(User user, String macAddress_user1) {


        UpdateStatus updateStatus = new UpdateStatus(macAddress_user1, user.getStatus());

        Call<RequestStatus> call = apiInterface.updateStatus(updateStatus);

        call.enqueue(new Callback<RequestStatus>() {
            @Override
            public void onResponse(Call<RequestStatus> call, Response<RequestStatus> response) {
                Log.e(TAG, response.body().getMsg());
            }

            @Override
            public void onFailure(Call<RequestStatus> call, Throwable t) {

            }
        });
    }

}
