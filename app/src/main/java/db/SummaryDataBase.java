package db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import dao.SummaryDao;
import table.Summary;

@Database(entities = {Summary.class}, version = 3)
public abstract class SummaryDataBase extends RoomDatabase {
    private static SummaryDataBase instance;

    public abstract SummaryDao summaryDao();

    public static synchronized SummaryDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SummaryDataBase.class, "summary_database")
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
        private SummaryDao summaryDao;

        private PopulateDbAsyncTask(SummaryDataBase db) {
            summaryDao = db.summaryDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
