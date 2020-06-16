package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import table.AppInfo;

@Dao
public interface AppInfoDao {

    @Insert
    void insert(AppInfo appInfo);

    @Update
    void update(AppInfo appInfo);

    @Delete
    void delete(AppInfo appInfo);

    @Query("DELETE FROM app_info_table")
    void deleteAllAppInfos();

    @Query("SELECT * FROM app_info_table")
    LiveData<List<AppInfo>> getAllAppInfos();
}
