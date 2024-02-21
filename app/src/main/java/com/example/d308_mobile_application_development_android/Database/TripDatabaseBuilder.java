package com.example.d308_mobile_application_development_android.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308_mobile_application_development_android.DAO.ExcursionDAO;
import com.example.d308_mobile_application_development_android.DAO.VacationDAO;
import com.example.d308_mobile_application_development_android.Entities.Excursion;
import com.example.d308_mobile_application_development_android.Entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
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
