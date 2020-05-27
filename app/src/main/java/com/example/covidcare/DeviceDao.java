package com.example.covidcare;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert
    void insert(Device note);

    @Update
    void update(Device note);

    @Delete
    void delete(Device note);

    @Query("DELETE FROM device_table")
    void deleteAllDevices();

    @Query("SELECT * FROM device_table ORDER BY name DESC")
    LiveData<List<Device>> getAllDevices();
}
