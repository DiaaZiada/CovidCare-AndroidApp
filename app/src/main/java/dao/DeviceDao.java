package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import table.Device;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert
    void insert(Device device);

    @Update
    void update(Device device);

    @Delete
    void delete(Device device);

    @Query("DELETE FROM device_table")
    void deleteAllDevices();

    @Query("SELECT * FROM device_table ORDER BY time DESC")
    LiveData<List<Device>> getAllDevices();
}