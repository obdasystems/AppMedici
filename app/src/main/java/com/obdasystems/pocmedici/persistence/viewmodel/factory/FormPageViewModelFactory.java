package com.obdasystems.pocmedici.persistence.viewmodel.factory;

import android.app.Application;

import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormPageViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FormPageViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private int mPageId;

    public FormPageViewModelFactory(Application application, int pageId) {
        mApplication = application;
        mPageId = pageId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CtcaeFormPageViewModel(mApplication, mPageId);
    }
}