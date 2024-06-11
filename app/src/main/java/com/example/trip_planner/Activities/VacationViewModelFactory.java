package com.example.trip_planner.Activities;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trip_planner.Database.Repository;

public class VacationViewModelFactory implements ViewModelProvider.Factory {

    private Repository repository;

    public VacationViewModelFactory(Repository repository) {
        this.repository = repository;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VacationViewModel.class)) {
            return (T) new VacationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}