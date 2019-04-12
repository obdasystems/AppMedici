package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.network.RestForm;

import java.util.List;

public interface DownloadedFormsAsyncResponse {
    public void getFormsQueryAsyncTaskFinished(List<RestForm> restForms);
}
