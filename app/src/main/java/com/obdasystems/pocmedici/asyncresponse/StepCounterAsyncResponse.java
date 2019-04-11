package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.persistence.entities.StepCounter;

public interface StepCounterAsyncResponse {

    public void getTodayStepCounterTaskFinished(StepCounter sp);

}
