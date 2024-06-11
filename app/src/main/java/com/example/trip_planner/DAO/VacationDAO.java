package com.example.trip_planner.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trip_planner.Entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);
    @Update
    void update(Vacation vacation);
    @Delete
    void delete(Vacation vacation);
    @Query("SELECT * FROM  VACATIONS ORDER BY vacationID ASC")
    LiveData<List<Vacation>> getAllVacations();
    @Query("SELECT * FROM VACATIONS WHERE vacationID = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);
}
