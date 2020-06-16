package com.example.covidcare;

import android.app.Application;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import table.AppInfo;
import table.Device;
import table.LocationTime;
import table.Meeting;
import table.User;

public class ModelView extends AndroidViewModel {
    private static final String TAG = "ModelView";

    public Repository getRepository() {
        return repository;
    }

    private Repository repository;
    private LiveData<List<Device>> allDevices;
    private LiveData<List<User>> allUsers;
    private LiveData<List<Meeting>> allMeetings;
    private LiveData<List<LocationTime>> allLocationsTimes;
    private LiveData<List<AppInfo>> allAppInfos;


    public ServiceConnection getServiceConnection() {
        return repository.getServiceConnection();
    }

    public LiveData<LocationUpdatesService.LocalBinder> getBinder() {
        return repository.getBinder();
    }



//    public ServiceConnection getServiceConnection() {
//        return repository.getServiceConnection();
//    }

//    public LiveData<MyService.MyBinder> getBinder() {
//        return repository.getBinder();
//    }


    public ModelView(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);

        allDevices = repository.getAllDevices();
        allUsers = repository.getAllUsers();
        allMeetings = repository.getAllMeetings();
        allLocationsTimes = repository.getAllLocationsTimes();
        allAppInfos = repository.getAllAppInfos();
    }

    /* AppInfo */
    public void appInfoInsert(AppInfo appInfo) {
        repository.appInfoInsert(appInfo);
    }

    public void appInfoUpdate(AppInfo appInfo) {
        repository.appInfoUpdate(appInfo);
    }

    public void appInfoDelete(AppInfo appInfo) {
        repository.appInfoDelete(appInfo);
    }

    public void deleteAllappInfos() {
        repository.deleteAllAppInfos();
    }

    public LiveData<List<AppInfo>> getAllappInfos() {
        return allAppInfos;
    }
    /* End appInfo*/


    /* LocationTime */
    public void locationTimeInsert(LocationTime locationTime) {
        repository.locationTimeInsert(locationTime);
    }

    public void locationTimeUpdate(LocationTime locationTime) {
        repository.locationTimeUpdate(locationTime);
    }

    public void locationTimeDelete(LocationTime locationTime) {
        repository.locationTimeDelete(locationTime);
    }

    public void DeleteAllLocationsTimes() {
        repository.deleteAllLocationsTimes();
    }

    public LiveData<List<LocationTime>> getAllLocationsTimes() {
        return allLocationsTimes;
    }
    /* End LocationTime */
    
    /* Meeting */
    public void meetingInsert(Meeting meeting) {
        repository.meetingInsert(meeting);
    }

    public void meetingUpdate(Meeting meeting) {
        repository.meetingUpdate(meeting);
    }

    public void meetinDelete(Meeting meeting) {
        repository.meetingDelete(meeting);
    }

    public void meetingDeleteAllMeetings() {
        repository.deleteAllMeetings();
    }

    public LiveData<List<Meeting>> getAllMeetings() {
        return allMeetings;
    }
    /* End Meeting*/


    /* User */
    public void userInsert(User user) {
        repository.userInsert(user);
    }

    public void userUpdate(User user) {
        repository.userUpdate(user);
    }

    public void userDelete(User user) {
        repository.userDelete(user);
    }

    public void deleteAllUsers() {
        repository.deletAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }
    /* End User*/


    /* Device */
    public void deviceInsert(Device device) {
        repository.deviceInsert(device);
    }

    public void deviceUpdate(Device device) {
        repository.deviceUpdate(device);
    }

    public void deviceDelete(Device device) {
        repository.deviceDelete(device);
    }

    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }
    /* End Device*/

}