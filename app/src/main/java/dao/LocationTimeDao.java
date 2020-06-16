package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import table.LocationTime;


@Dao
public interface LocationTimeDao {
    @Insert
    void insert(LocationTime locationTime);

    @Update
    void update(LocationTime locationTime);

    @Delete
    void delete(LocationTime locationTime);

    @Query("DELETE FROM location_time_table")
    void deleteAllLocationsTimes();

    @Query("SELECT * FROM location_time_table ORDER BY time DESC")
    LiveData<List<LocationTime>> getAllLocationsTimes();
}
