package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;

import java.util.List;

public interface FormPagesAsyncResponse {
    void taskFinished(List<CtcaeFormPage> pages);

    void fillingProcessTaskFinished(int fillingProcessId);
}
