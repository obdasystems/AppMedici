package com.obdasystems.pocmedici.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://obdatest.dis.uniroma1.it:3000/api/";

    //public static <T> T createService(Class<?> serviceClass, String auth) { return null; }

    private ApiClient() {
        // empty
    }

    /**
     * Returns a {@link Builder} for the specified service.
     *
     * @param serviceClass the service class
     * @return a builder for the specified service
     */
    public static <T> Builder<T> forService(@NonNull Class<T> serviceClass) {
        return new Builder<>(serviceClass);
    }

    /**
     * Fluent API for creating {@code ApiClient}s.
     *
     * @param <T> the service class
     */
    public static class Builder<T> {
        private final Class<T> serviceClass;
        private final List<Interceptor> interceptors;
        private final OkHttpClient.Builder httpClient;
        private URL baseURL;

        Builder(@NonNull Class<T> serviceClass) {
            this.serviceClass = Objects.requireNonNull(
                    serviceClass, "serviceClass must not be null");
            interceptors = new ArrayList<>();
            httpClient = new OkHttpClient.Builder();
        }

        public Builder<T> baseURL(URL baseURL) {
            this.baseURL = Objects.requireNonNull(baseURL, "baseURL must not be null");
            return this;
        }

        public Builder<T> baseURL(String baseURL) {
            try {
                this.baseURL = new URL(Objects.requireNonNull(baseURL));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed baseURL", e);
            }
            return this;
        }

        public Builder<T> addInterceptor(@NonNull Interceptor interceptor) {
            this.interceptors.add(Objects.requireNonNull(
                    interceptor, "interceptor must not be null"));
            return this;
        }

        public Builder<T> logging(@NonNull HttpLoggingInterceptor.Level level) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(
                    Objects.requireNonNull(level, "level cannot be null"));
            this.interceptors.add(loggingInterceptor);
            return this;
        }

        public T build() {
            // Enable lenient mode
            Gson gson = new GsonBuilder().setLenient().create();

            // Add interceptors
            for (Interceptor i : interceptors) {
                httpClient.addInterceptor(i);
            }

            // Build service client
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL.toString())
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(serviceClass);
        }
    }

}
