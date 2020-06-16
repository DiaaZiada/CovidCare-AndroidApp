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

import dao.DeviceDao;
import dao.LocationTimeDao;
import dao.MeetingDao;
import dao.UserDao;
import db.DeviceDataBase;
import db.LocationTimeDataBase;
import db.MeetingDataBase;
import db.UserDataBase;
import table.Device;
import table.LocationTime;
import table.Meeting;
import table.User;

public class Repository {
    private static final String TAG = "DeviceRepository";

    private DeviceDao deviceDao;
    private UserDao userDao;
    private MeetingDao meetingDao;
    private LocationTimeDao locationTimeDao;

    private LiveData<List<Device>> allDevices;
    private LiveData<List<User>> allUsers;
    private LiveData<List<Meeting>> allMeetings;
    private LiveData<List<LocationTime>> allLocationsTimes;

//    private MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();
    private MutableLiveData<LocationUpdatesService.LocalBinder> mBinder = new MutableLiveData<>();

    private static Repository instance;

    public static Repository getInstance(Application application) {
        if (instance == null)
            instance = new Repository(application);
        return instance;
    }

    public static Repository getInstance() {
        return instance;
    }


    private Repository(Application application) {
        DeviceDataBase deviceDatabase = DeviceDataBase.getInstance(application);
        deviceDao = deviceDatabase.deviceDao();
        allDevices = deviceDao.getAllDevices();

        UserDataBase userDataBase = UserDataBase.getInstance(application);
        userDao = userDataBase.userDao();
        allUsers = userDao.getAllUsers();

        MeetingDataBase meetingDataBase = MeetingDataBase.getInstance(application);
        meetingDao = meetingDataBase.meetingDao();
        allMeetings = meetingDao.getAllMeetings();

        LocationTimeDataBase locationTimeDataBase = LocationTimeDataBase.getInstance(application);
        locationTimeDao = locationTimeDataBase.locationTimeDao();
        allLocationsTimes = locationTimeDao.getAllLocationsTimes();

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

    public void deleteAllLocationsTimes() {
        new DeleteAllLocationsTimesAsyncTask(locationTimeDao).execute();
    }

    public LiveData<List<LocationTime>> getAllLocationsTimes() {
        return allLocationsTimes;
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


    /*End LocationTime DataBase*/



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


    /*End Meeting DataBase*/

    /*User DataBase*/

    public void userInsert(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    public void userUpdate(User user) {
        new UpdateUserAsyncTask(userDao).execute(user);
    }

    public void userDelete(User user) {
        new DeleteUserAsyncTask(userDao).execute(user);
    }

    public void deletAllUsers() {
        new DeleteAllUsersAsyncTask(userDao).execute();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;

        private DeleteAllUsersAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            return null;
        }
    }

    /*End User DataBase*/



    /* Location Service*/

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) iBinder;
            mBinder.postValue(binder);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mBinder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public LiveData<LocationUpdatesService.LocalBinder> getBinder() {
        return mBinder;
    }

    /* End Location Service*/

    /* BlueTooth Service*/

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder iBinder) {
//            Log.d(TAG, "ServiceConnection: connected to service.");
//            // We've bound to MyService, cast the IBinder and get MyBinder instance
//            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
//            mBinder.postValue(binder);
//
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            Log.d(TAG, "ServiceConnection: disconnected from service.");
//            mBinder.postValue(null);
//        }
//    };
//
//    public ServiceConnection getServiceConnection() {
//        return serviceConnection;
//    }
//
//    public LiveData<MyService.MyBinder> getBinder() {
//        return mBinder;
//    }

    /* End BlueTooth Service*/


    /*Device DataBase*/

    public void deviceInsert(Device device) {
        new InsertDeviceAsyncTask(deviceDao).execute(device);
    }

    public void deviceUpdate(Device device) {
        new UpdateDeviceAsyncTask(deviceDao).execute(device);
    }

    public void deviceDelete(Device device) {
        new DeleteDeviceAsyncTask(deviceDao).execute(device);
    }

    public void deleteAllDevices() {
        new DeleteAllDevicesAsyncTask(deviceDao).execute();
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }

    private static class InsertDeviceAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private InsertDeviceAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.insert(devices[0]);
            return null;
        }
    }

    private static class UpdateDeviceAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private UpdateDeviceAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.update(devices[0]);
            return null;
        }
    }

    private static class DeleteDeviceAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;

        private DeleteDeviceAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.delete(devices[0]);
            return null;
        }
    }

    private static class DeleteAllDevicesAsyncTask extends AsyncTask<Void, Void, Void> {
        private DeviceDao deviceDao;

        private DeleteAllDevicesAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.deleteAllDevices();
            return null;
        }
    }

    /*End Device DataBase*/
}
