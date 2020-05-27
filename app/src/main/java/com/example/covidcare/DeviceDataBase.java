package com.example.covidcare;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Device.class}, version = 3)
public abstract class DeviceDataBase extends RoomDatabase {

    private  static  DeviceDataBase instance;
    public  abstract  DeviceDao deviceDao();

    public static synchronized DeviceDataBase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DeviceDataBase.class, "device_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private DeviceDao deviceDao;
        private PopulateDbAsyncTask(DeviceDataBase db) {
            deviceDao = db.deviceDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            deviceDao.insert(new Device("Title 1", "Description 1", "1"));
            deviceDao.insert(new Device("Title 2", "Description 2", "1"));
            deviceDao.insert(new Device("Title 3", "Description 3", "1"));
            return null;
        }
    }


}
