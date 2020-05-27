package com.example.covidcare;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DeviceRepository {
    private DeviceDao deviceDao;
    private LiveData<List<Device>> allDevices;


    public DeviceRepository(Application application) {
        DeviceDataBase database = DeviceDataBase.getInstance(application);
        deviceDao = database.deviceDao();
        allDevices = deviceDao.getAllDevices();
    }
    public void insert(Device device) {
        new InsertDeviceAsyncTask(deviceDao).execute(device);
    }
    public void update(Device device) {
        new UpdateNoteAsyncTask(deviceDao).execute(device);
    }
    public void delete(Device device) {
        new DeleteNoteAsyncTask(deviceDao).execute(device);
    }
    public void deleteAllNotes() {
        new DeleteAllDevicesAsyncTask(deviceDao).execute();
    }
    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }
    private static class InsertDeviceAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao noteDao;
        private InsertDeviceAsyncTask(DeviceDao deviceDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Device... devices) {
            noteDao.insert(devices[0]);
            return null;
        }
    }
    private static class UpdateNoteAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao noteDao;
        private UpdateNoteAsyncTask(DeviceDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Device... devices) {
            noteDao.update(devices[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Device, Void, Void> {
        private DeviceDao deviceDao;
        private DeleteNoteAsyncTask(DeviceDao deviceDao) {
            this.deviceDao = deviceDao;
        }
        @Override
        protected Void doInBackground(Device... devices) {
            deviceDao.delete(devices[0]);
            return null;
        }
    }
    private static class DeleteAllDevicesAsyncTask extends AsyncTask<Void, Void, Void> {
        private DeviceDao noteDao;
        private DeleteAllDevicesAsyncTask(DeviceDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllDevices();
            return null;
        }
    }
}
