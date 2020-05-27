package com.example.covidcare;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ModelView extends AndroidViewModel {
    private DeviceRepository repository;
    private LiveData<List<Device>> allDevices;
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
    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }
    public LiveData<List<Device>> getAllNotes() {
        return allDevices;
    }
}