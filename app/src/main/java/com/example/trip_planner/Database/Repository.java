package com.example.trip_planner.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trip_planner.DAO.ExcursionDAO;
import com.example.trip_planner.DAO.VacationDAO;
import com.example.trip_planner.Entities.Excursion;
import com.example.trip_planner.Entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private VacationDAO vacationDAO;
    private ExcursionDAO excursionDAO;
    private LiveData<List<Vacation>> allVacations;
    private LiveData<List<Excursion>> allExcursions;
    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository (Application application) {
        TripDatabaseBuilder db = TripDatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
        allVacations = vacationDAO.getAllVacations();
        allExcursions = excursionDAO.getAllExcursions();
    }

    public LiveData<List<Vacation>> getAllVacations() {
        return allVacations;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.insert(vacation);
        });
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.update(vacation);
        });
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.delete(vacation);
        });
    }

    public LiveData<List<Excursion>> getAllExcursions() {
        return allExcursions;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.insert(excursion);
        });
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.update(excursion);
        });
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.delete(excursion);
        });
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return vacationDAO.getVacationById(vacationId);
    }
}
