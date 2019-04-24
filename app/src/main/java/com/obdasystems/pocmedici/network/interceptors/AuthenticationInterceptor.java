package com.obdasystems.pocmedici.network.interceptors;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.activity.MainActivity;
import com.obdasystems.pocmedici.utils.AppPreferences;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.obdasystems.pocmedici.utils.AppPreferences.AUTHORIZATION_TOKEN;

/**
 * Interceptor that handles authentication in the ITCO API requests.
 */
public class AuthenticationInterceptor implements Interceptor {
    private final Context context;

    public AuthenticationInterceptor(@NonNull Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null");
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String token = AppPreferences.with(context)
                .getString(AUTHORIZATION_TOKEN);
        if (token != null && !token.isEmpty()) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", String.format("Bearer %s", token));

            Request request = builder.build();
            Response response = chain.proceed(request);

            if (!(response.code() >= 400 && response.code() < 500)) {
                return response;
            } else {
                AppPreferences.with(context).remove(AUTHORIZATION_TOKEN);
            }
        }

        // Otherwise trigger application restart
        context.startActivity(new Intent(context, MainActivity.class));
        return chain.proceed(chain.request());
    }

}
