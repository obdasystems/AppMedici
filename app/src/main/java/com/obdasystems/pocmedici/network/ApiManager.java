package com.obdasystems.pocmedici.network;

/**
 * Singleton collector of all the APIs supported by the application.
 */
public class ApiManager {
    private static final MediciApiInterface ITCO_API =
            MediciApiClient.getClient().create(MediciApiInterface.class);

    private ApiManager() {

    }

    public static MediciApiInterface mediciApi() {
        return ITCO_API;
    }

}
