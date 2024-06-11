package com.example.trip_planner.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trip_planner.DAO.ExcursionDAO;
import com.example.trip_planner.DAO.VacationDAO;
import com.example.trip_planner.Entities.Excursion;
import com.example.trip_planner.Entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 2, exportSchema = false)
public abstract class TripDatabaseBuilder extends RoomDatabase {

    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    public static volatile TripDatabaseBuilder INSTANCE;
    static TripDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TripDatabaseBuilder.class, "MyTripDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}