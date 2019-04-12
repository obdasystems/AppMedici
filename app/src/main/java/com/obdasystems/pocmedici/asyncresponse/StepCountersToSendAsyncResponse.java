package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.persistence.entities.StepCounter;

import java.util.List;

public interface StepCountersToSendAsyncResponse {
    public void getStepCounterToSendQueryAsyncTaskFinished(List<StepCounter> stepCounters);

}
