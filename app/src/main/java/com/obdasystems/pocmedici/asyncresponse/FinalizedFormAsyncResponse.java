package com.obdasystems.pocmedici.asyncresponse;

import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;

public interface FinalizedFormAsyncResponse {

    public void getFinalizedTaskFinished(CtcaeFormFillingProcess finalized);
}
