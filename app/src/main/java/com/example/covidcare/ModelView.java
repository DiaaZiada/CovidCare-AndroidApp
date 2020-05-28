package com.example.covidcare;

import android.app.Application;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ModelView extends AndroidViewModel {
    private static final String TAG = "ModelView";

    public Repository getRepository() {
        return repository;
    }

    private Repository repository;
    private LiveData<List<Device>> allDevices;


    public ServiceConnection getServiceConnection(){
        return repository.getServiceConnection();
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return repository.getBinder();
    }





    public ModelView(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        allDevices = repository.getAllDevices();
    }


    public void insert(Device device) {
        repository.deviceInsert(device);
    }
    public void update(Device device) {
        repository.deviceUpdate(device);
    }
    public void delete(Device device) {
        repository.deviceDelete(device);
    }
    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }
    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }


}