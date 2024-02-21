package com.example.d308_mobile_application_development_android.Database;

import android.app.Application;

import com.example.d308_mobile_application_development_android.DAO.ExcursionDAO;
import com.example.d308_mobile_application_development_android.DAO.VacationDAO;
import com.example.d308_mobile_application_development_android.Entities.Excursion;
import com.example.d308_mobile_application_development_android.Entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private VacationDAO vacationDAO;
    private ExcursionDAO excursionDAO;
    private List<Vacation> allVacations;
    private List<Excursion> allExcursions;
    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository (Application application) {
        TripDatabaseBuilder db = TripDatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
    }

    public List<Vacation> getAllVacations() {
        databaseExecutor.execute(() -> {
            allVacations = vacationDAO.getAllVacations();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return allVacations;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.insert(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.update(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> {
            vacationDAO.delete(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Excursion> getAllExcursions() {
        databaseExecutor.execute(() -> {
            allExcursions = excursionDAO.getAllExcursions();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return allExcursions;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.insert(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.update(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> {
            excursionDAO.delete(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
