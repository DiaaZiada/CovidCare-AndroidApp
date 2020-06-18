package com.example.covidcare;

import android.app.Application;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import table.AppInfo;
import table.LocationTime;
import table.Meeting;

public class ModelView extends AndroidViewModel {
    private static final String TAG = "ModelView";

    public Repository getRepository() {
        return repository;
    }

    private Repository repository;
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

    public boolean isBound() {
        return repository.isBound();
    }

    public void setBound(boolean bound) {
        repository.setBound(bound);
    }

}