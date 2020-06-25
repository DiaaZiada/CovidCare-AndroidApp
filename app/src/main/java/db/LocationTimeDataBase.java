package db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import dao.LocationTimeDao;
import table.LocationTime;

@Database(entities = {LocationTime.class}, version = 1)

public abstract class LocationTimeDataBase extends RoomDatabase {

    private static LocationTimeDataBase instance;
    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static synchronized LocationTimeDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LocationTimeDataBase.class, "location_time_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    public abstract LocationTimeDao locationTimeDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private LocationTimeDao locationTimeDao;

        private PopulateDbAsyncTask(LocationTimeDataBase db) {
            locationTimeDao = db.locationTimeDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
