package com.example.covidcare;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import dao.AppInfoDao;
import dao.LocationTimeDao;
import dao.MeetingDao;
import db.AppInfoDataBase;
import db.LocationTimeDataBase;
import db.MeetingDataBase;
import table.AppInfo;
import table.LocationTime;
import table.Meeting;

public class Repository {
    private static final String TAG = "DeviceRepository";
    private static boolean bound;
    private static Repository instance;
    private MeetingDao meetingDao;
    private LocationTimeDao locationTimeDao;
    private AppInfoDao appInfoDao;
    private LiveData<List<Meeting>> allMeetings;
    private LiveData<List<LocationTime>> allLocationsTimes;
    private LiveData<List<AppInfo>> allAppInfos;
    //    private MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();
    private MutableLiveData<LocationUpdatesService.LocalBinder> mBinder = new MutableLiveData<>();
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) iBinder;
            mBinder.postValue(binder);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mBinder.postValue(null);
            bound = false;
        }
    };

    private Repository(Application application) {

        MeetingDataBase meetingDataBase = MeetingDataBase.getInstance(application);
        meetingDao = meetingDataBase.meetingDao();
        allMeetings = meetingDao.getAllMeetings();

        LocationTimeDataBase locationTimeDataBase = LocationTimeDataBase.getInstance(application);
        locationTimeDao = locationTimeDataBase.locationTimeDao();
        allLocationsTimes = locationTimeDao.getAllLocationsTimes();


        AppInfoDataBase appInfoDataBase = AppInfoDataBase.getInstance(application);
        appInfoDao = appInfoDataBase.appInfoDao();
        allAppInfos = appInfoDao.getAllAppInfos();

        bound = false;


    }

    public static Repository getInstance(Application application) {
        if (instance == null)
            instance = new Repository(application);
        return instance;
    }

    /*AppInfo DataBase*/

    public static Repository getInstance() {
        return instance;
    }

    public void appInfoInsert(AppInfo appInfo) {
        new InsertAppInfoAsyncTask(appInfoDao).execute(appInfo);
    }

    public void appInfoUpdate(AppInfo appInfo) {
        new UpdateAppInfoAsyncTask(appInfoDao).execute(appInfo);
    }

    public void appInfoDelete(AppInfo appInfo) {
        new DeleteAppInfoAsyncTask(appInfoDao).execute(appInfo);
    }

    public void deleteAllAppInfos() {
        new DeleteAllAppInfosAsyncTask(appInfoDao).execute();
    }

    public LiveData<List<AppInfo>> getAllAppInfos() {
        return allAppInfos;
    }

    /*LocationTime DataBase*/
    public void locationTimeInsert(LocationTime locationTime) {
        new InsertLocationTimeAsyncTask(locationTimeDao).execute(locationTime);
    }

    public void locationTimeUpdate(LocationTime locationTime) {
        new UpdateLocationTimeAsyncTask(locationTimeDao).execute(locationTime);
    }

    public void locationTimeDelete(LocationTime locationTime) {
        new DeleteLocationTimeAsyncTask(locationTimeDao).execute(locationTime);
    }

    /*End AppInfo DataBase*/

    public void deleteAllLocationsTimes() {
        new DeleteAllLocationsTimesAsyncTask(locationTimeDao).execute();
    }

    public LiveData<List<LocationTime>> getAllLocationsTimes() {
        return allLocationsTimes;
    }

    /*Meeting DataBase*/
    public void meetingInsert(Meeting meeting) {
        new InsertMeetingAsyncTask(meetingDao).execute(meeting);
    }

    public void meetingUpdate(Meeting meeting) {
        new UpdateMeetingAsyncTask(meetingDao).execute(meeting);
    }

    public void meetingDelete(Meeting meeting) {
        new DeleteMeetingAsyncTask(meetingDao).execute(meeting);
    }

    public void deleteAllMeetings() {
        new DeleteAllMeetingsAsyncTask(meetingDao).execute();
    }

    public LiveData<List<Meeting>> getAllMeetings() {
        return allMeetings;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public LiveData<LocationUpdatesService.LocalBinder> getBinder() {
        return mBinder;
    }


    /*End LocationTime DataBase*/

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        Repository.bound = bound;
    }

    private static class InsertAppInfoAsyncTask extends AsyncTask<AppInfo, Void, Void> {
        private AppInfoDao appInfoDao;

        private InsertAppInfoAsyncTask(AppInfoDao appInfoDao) {
            this.appInfoDao = appInfoDao;
        }

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            appInfoDao.insert(appInfos[0]);
            return null;
        }
    }

    private static class UpdateAppInfoAsyncTask extends AsyncTask<AppInfo, Void, Void> {
        private AppInfoDao appInfoDao;

        private UpdateAppInfoAsyncTask(AppInfoDao appInfoDao) {
            this.appInfoDao = appInfoDao;
        }

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            appInfoDao.update(appInfos[0]);
            return null;
        }
    }

    private static class DeleteAppInfoAsyncTask extends AsyncTask<AppInfo, Void, Void> {
        private AppInfoDao appInfoDao;

        private DeleteAppInfoAsyncTask(AppInfoDao appInfoDao) {
            this.appInfoDao = appInfoDao;
        }

        @Override
        protected Void doInBackground(AppInfo... appInfos) {
            appInfoDao.delete(appInfos[0]);
            return null;
        }
    }

    private static class DeleteAllAppInfosAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppInfoDao appInfoDao;

        private DeleteAllAppInfosAsyncTask(AppInfoDao appInfoDao) {
            this.appInfoDao = appInfoDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appInfoDao.deleteAllAppInfos();
            return null;
        }
    }

    private static class InsertLocationTimeAsyncTask extends AsyncTask<LocationTime, Void, Void> {
        private LocationTimeDao locationTimeDao;

        private InsertLocationTimeAsyncTask(LocationTimeDao locationTimeDao) {
            this.locationTimeDao = locationTimeDao;
        }

        @Override
        protected Void doInBackground(LocationTime... locationsTimes) {
            locationTimeDao.insert(locationsTimes[0]);
            return null;
        }
    }

    private static class UpdateLocationTimeAsyncTask extends AsyncTask<LocationTime, Void, Void> {
        private LocationTimeDao locationTimeDao;

        private UpdateLocationTimeAsyncTask(LocationTimeDao locationTimeDao) {
            this.locationTimeDao = locationTimeDao;
        }

        @Override
        protected Void doInBackground(LocationTime... locationsTimes) {
            locationTimeDao.update(locationsTimes[0]);
            return null;
        }
    }

    private static class DeleteLocationTimeAsyncTask extends AsyncTask<LocationTime, Void, Void> {
        private LocationTimeDao locationTimeDao;

        private DeleteLocationTimeAsyncTask(LocationTimeDao locationTimeDao) {
            this.locationTimeDao = locationTimeDao;
        }

        @Override
        protected Void doInBackground(LocationTime... locationsTimes) {
            locationTimeDao.delete(locationsTimes[0]);
            return null;
        }
    }


    /*End Meeting DataBase*/





    /* Location Service*/

    private static class DeleteAllLocationsTimesAsyncTask extends AsyncTask<Void, Void, Void> {
        private LocationTimeDao locationTimeDao;

        private DeleteAllLocationsTimesAsyncTask(LocationTimeDao locationTimeDao) {
            this.locationTimeDao = locationTimeDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            locationTimeDao.deleteAllLocationsTimes();
            return null;
        }
    }

    private static class InsertMeetingAsyncTask extends AsyncTask<Meeting, Void, Void> {
        private MeetingDao meetingDao;

        private InsertMeetingAsyncTask(MeetingDao meetingDao) {
            this.meetingDao = meetingDao;
        }

        @Override
        protected Void doInBackground(Meeting... meetings) {
            meetingDao.insert(meetings[0]);
            return null;
        }
    }

    private static class UpdateMeetingAsyncTask extends AsyncTask<Meeting, Void, Void> {
        private MeetingDao meetingDao;

        private UpdateMeetingAsyncTask(MeetingDao meetingDao) {
            this.meetingDao = meetingDao;
        }

        @Override
        protected Void doInBackground(Meeting... meetings) {
            meetingDao.update(meetings[0]);
            return null;
        }
    }

    /* End Location Service*/

    private static class DeleteMeetingAsyncTask extends AsyncTask<Meeting, Void, Void> {
        private MeetingDao meetingDao;

        private DeleteMeetingAsyncTask(MeetingDao meetingDao) {
            this.meetingDao = meetingDao;
        }

        @Override
        protected Void doInBackground(Meeting... meetings) {
            meetingDao.delete(meetings[0]);
            return null;
        }
    }

    private static class DeleteAllMeetingsAsyncTask extends AsyncTask<Void, Void, Void> {
        private MeetingDao meetingDao;

        private DeleteAllMeetingsAsyncTask(MeetingDao meetingDao) {
            this.meetingDao = meetingDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            meetingDao.deleteAllMeetings();
            return null;
        }
    }
}
