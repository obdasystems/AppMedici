package com.obdasystems.pocmedici.network;

import org.junit.Test;

import java.io.IOException;

import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClientTest {

    @Test
    public void forService() throws IOException {
        ItcoService service = (ItcoService) ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .build();
        System.out.println(service.getCalendarEvents().execute().body());
    }

}