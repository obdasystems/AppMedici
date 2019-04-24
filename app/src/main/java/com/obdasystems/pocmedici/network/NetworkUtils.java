package com.obdasystems.pocmedici.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;
import com.obdasystems.pocmedici.network.request.LoginResponse;
import com.obdasystems.pocmedici.utils.AppPreferences;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.obdasystems.pocmedici.utils.AppPreferences.AUTHORIZATION_ISSUE;
import static com.obdasystems.pocmedici.utils.AppPreferences.AUTHORIZATION_ISSUE_DESCRIPTION;
import static com.obdasystems.pocmedici.utils.AppPreferences.AUTHORIZATION_TOKEN;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static void requestNewAuthorizationToken(@NonNull String password,
                                                    @NonNull String username,
                                                    @NonNull Context context) {
        ItcoService apiService = ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(new AuthenticationInterceptor(context))
                .build();

        apiService.requestAuthentication(password, username)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            String token = response.body().getAccessToken();
                            Log.i(TAG, "Authentication Token received: " + token);
                            AppPreferences.with(context)
                                    .set(AUTHORIZATION_TOKEN, token)
                                    .set(AUTHORIZATION_ISSUE, false)
                                    .set(AUTHORIZATION_ISSUE_DESCRIPTION, (String) null);
                        } else {
                            Log.e(TAG, "Unable to authenticate");
                            AppPreferences.with(context)
                                    .set(AUTHORIZATION_ISSUE, true)
                                    .set(AUTHORIZATION_ISSUE_DESCRIPTION,
                                            "Unable to authenticate");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e(TAG, "Unable to authenticate: ", t);
                        AppPreferences.with(context)
                                .set(AUTHORIZATION_ISSUE, true)
                                .set(AUTHORIZATION_ISSUE_DESCRIPTION,
                                        "Unable to authenticate (" + t.getMessage() + ")");
                    }
                });
    }

}
