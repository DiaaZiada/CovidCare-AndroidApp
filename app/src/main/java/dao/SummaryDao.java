package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import table.Summary;

@Dao
public interface SummaryDao {
    @Insert
    void insert(Summary summary);

    @Update
    void update(Summary summary);

    @Delete
    void delete(Summary summary);

    @Query("DELETE FROM `summary _table`")
    void deleteAllSummaries();

    @Query("SELECT * FROM `summary _table`")
    LiveData<List<Summary>> getAllSummaries();
}
