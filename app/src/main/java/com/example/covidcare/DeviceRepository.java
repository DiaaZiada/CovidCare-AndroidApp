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

public class DeviceRepository {
    private static final String TAG = "DeviceRepository";
    private DeviceDao deviceDao;
    private LiveData<List<Device>> allDevices;

    private MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();

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



    public DeviceRepository(Application application) {
        DeviceDataBase database = DeviceDataBase.getInstance(application);
        deviceDao = database.deviceDao();
        allDevices = deviceDao.getAllDevices();
    }

    public void insert(Device device) {
        new InsertDeviceAsyncTask(deviceDao).execute(device);
    }
    public void update(Device device) {
        new UpdateDeviceAsyncTask(deviceDao).execute(device);
    }
    public void delete(Device device) {
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
}
