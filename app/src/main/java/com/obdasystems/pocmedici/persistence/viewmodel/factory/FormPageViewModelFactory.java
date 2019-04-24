package com.obdasystems.pocmedici.persistence.viewmodel.factory;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormPageViewModel;

public class FormPageViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private int mPageId;

    public FormPageViewModelFactory(Application application, int pageId) {
        mApplication = application;
        mPageId = pageId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CtcaeFormPageViewModel(mApplication, mPageId);
    }

}