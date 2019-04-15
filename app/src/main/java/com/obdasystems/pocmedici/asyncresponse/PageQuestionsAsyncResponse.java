package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.activity.FormPageActivity;
import com.obdasystems.pocmedici.network.RestFilledForm;

import java.util.List;

public interface PageQuestionsAsyncResponse {
    public void getQuestionsTaskFinished(FormPageActivity.FormQuestionsContainer container);

    public void getUnansweredQuestionsTaskFinished(FormPageActivity.IncompleteContainer container);

    public void finalizeFillingProcessTaskFinished(RestFilledForm result);

    public void deleteFillingProcessTaskFinished();
}
