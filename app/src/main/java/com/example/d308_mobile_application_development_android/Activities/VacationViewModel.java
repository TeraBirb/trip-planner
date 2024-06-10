package com.example.d308_mobile_application_development_android.Activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.d308_mobile_application_development_android.Database.Repository;
import com.example.d308_mobile_application_development_android.Entities.Vacation;

import java.util.List;

public class VacationViewModel extends ViewModel {

    private Repository repository;
    private LiveData<List<Vacation>> allVacations;
    private LiveData<Vacation> vacation;

    public VacationViewModel(Repository repository) {
        this.repository = repository;
        allVacations = repository.getAllVacations();
    }

    public LiveData<List<Vacation>> getAllVacations() {
        return allVacations;
    }

    public void insert(Vacation vacation) {
        repository.insert(vacation);
    }

    public void update(Vacation vacation) {
        repository.update(vacation);
    }

    public void delete(Vacation vacation) {
        repository.delete(vacation);
    }

    public LiveData<Vacation> getVacation() {
        return vacation;
    }

    public void loadVacation(int vacationId) {
        vacation = repository.getVacationById(vacationId);
    }
}
