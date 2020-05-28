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

public class Repository {
    private static final String TAG = "DeviceRepository";
    private DeviceDao deviceDao;
    private  UserDao userDao;
    private LiveData<List<Device>> allDevices;
    private LiveData<List<User>> allUsers;
    private MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();

    public Repository(Application application) {
        DeviceDataBase deviceDatabase = DeviceDataBase.getInstance(application);
        deviceDao = deviceDatabase.deviceDao();
        allDevices = deviceDao.getAllDevices();

//        UserDataBase userDataBase = UserDataBase.getInstance(application);
//        userDao = userDataBase.userDao();
//        allUsers = userDao.getAllUsers();


    }

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
    public void AllUsers() {
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



    /* BlueTooth Service*/

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mBinder.postValue(binder);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mBinder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return mBinder;
    }

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
