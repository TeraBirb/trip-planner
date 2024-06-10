package com.example.d308_mobile_application_development_android.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308_mobile_application_development_android.Entities.Vacation;

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
