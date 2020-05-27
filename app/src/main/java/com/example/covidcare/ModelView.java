package com.example.covidcare;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ModelView extends AndroidViewModel {
    private static final String TAG = "ModelView";

    private DeviceRepository repository;
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





    public ModelView(@NonNull Application application) {
        super(application);
        repository = new DeviceRepository(application);
        allDevices = repository.getAllDevices();
    }


    public void insert(Device device) {
        repository.insert(device);
    }
    public void update(Device device) {
        repository.update(device);
    }
    public void delete(Device device) {
        repository.delete(device);
    }
    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }
    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }


}