package com.obdasystems.pocmedici.network;

/**
 * Singleton collector of all the APIs supported by the application.
 */
public class ApiManager {
    private static final MediciApi ITCO_API =
            MediciApiClient.getClient().create(MediciApi.class);

    private ApiManager() {

    }

    public static MediciApi mediciApi() {
        return ITCO_API;
    }

}
