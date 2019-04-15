package com.obdasystems.pocmedici.network;

import java.util.List;

public class RestDrugList {

    private List<RestDrug> drugs;

    public RestDrugList(List<RestDrug> drugs) {
        this.drugs = drugs;
    }

    public List<RestDrug> getDrugs() {
        return drugs;
    }

    public void setDrugs(List<RestDrug> drugs) {
        this.drugs = drugs;
    }
}
