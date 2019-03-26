package com.obdasystems.pocmedici.persistence.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;

import java.util.List;

public class StepCountersViewModel extends AndroidViewModel {

    private StepCounterRepository repository;
    private LiveData<List<StepCounter>> allCounters;

    public StepCountersViewModel (Application app) {
        super(app);
        repository = new StepCounterRepository(app);
        allCounters = repository.getAllStepCounters();
        //Log.i("ROOM","CtcaeFormViewModel "+allForms.getValue().size());
    }

    public LiveData<List<StepCounter>> getAllCounters() {
        return allCounters;
    }
}
