package com.example.trip_planner.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trip_planner.Entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);
    @Update
    void update(Excursion excursion);
    @Delete
    void delete(Excursion excursion);
    @Query("SELECT * FROM EXCURSIONS ORDER BY excursionID ASC")
    LiveData<List<Excursion>> getAllExcursions();
}
