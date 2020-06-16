package db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import dao.AppInfoDao;
import table.AppInfo;

@Database(entities = {AppInfo.class}, version = 1)
public abstract class AppInfoDataBase extends RoomDatabase {
    private static AppInfoDataBase instance;

    public abstract AppInfoDao appInfoDao();

    public static synchronized AppInfoDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppInfoDataBase.class, "app_info_database")
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
        private AppInfoDao appInfoDao;

        private PopulateDbAsyncTask(AppInfoDataBase db) {
            appInfoDao = db.appInfoDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
