package com.obdasystems.pocmedici.persistence.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;

import java.util.List;

public class CtcaeFormListViewModel extends AndroidViewModel {

    private CtcaeFormRepository repository;
    private LiveData<List<JoinFormWithMaxPageNumberData>> allForms;

    public CtcaeFormListViewModel (Application app) {
        super(app);
        repository = new CtcaeFormRepository(app);
        allForms = repository.getAllForms();
        //Log.i("ROOM","CtcaeFormViewModel "+allForms.getValue().size());
    }

    public LiveData<List<JoinFormWithMaxPageNumberData>> getAllForms() {
        return allForms;
    }

    /*public void insertForm(CtcaeForm form) {
        repository.insertForm(form);
    }*/
}
