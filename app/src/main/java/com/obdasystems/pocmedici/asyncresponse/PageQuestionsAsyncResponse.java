package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.activity.NewFormPageActivity;
import com.obdasystems.pocmedici.network.RestFilledForm;

public interface PageQuestionsAsyncResponse {
    public void getQuestionsTaskFinished(NewFormPageActivity.FormQuestionsContainer container);

    public void getUnansweredQuestionsTaskFinished(NewFormPageActivity.IncompleteContainer container);

    public void finalizeFillingProcessTaskFinished(RestFilledForm result);

    public void deleteFillingProcessTaskFinished();
}
