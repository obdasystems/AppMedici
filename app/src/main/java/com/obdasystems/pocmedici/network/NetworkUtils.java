package com.obdasystems.pocmedici.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.obdasystems.pocmedici.network.request.LoginResponse;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtils {

    public static void requestNewAuthorizationToken(String pwd, String usr, Context context) {

        MediciApi apiService = MediciApiClient.createService(MediciApi.class, "");

        Call<LoginResponse> call = apiService.requestAuthentication(pwd,usr);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {
                    String token = response.body().getAccessToken();
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Authentication Token received: "+token);
                    SaveSharedPreference.setAuthorizationToken(context, token);
                    SaveSharedPreference.setAuthorizationIssue(context, false);
                }
                else {
                    switch (response.code()) {
                        case 401:
                            Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate (401)");
                            Toast.makeText(context, "Unable to authenticate (401)", Toast.LENGTH_LONG).show();
                            SaveSharedPreference.setAuthorizationIssue(context, true);
                            SaveSharedPreference.setAuthorizationIssueDescription(context, "Unable to authenticate (401)");
                            break;
                        case 404:
                            Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate (404)");
                            Toast.makeText(context, "Unable to authenticate (404)", Toast.LENGTH_LONG).show();
                            SaveSharedPreference.setAuthorizationIssue(context, true);
                            SaveSharedPreference.setAuthorizationIssueDescription(context, "Unable to authenticate (404)");
                            break;
                        case 500:
                            Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate (500)");
                            Toast.makeText(context, "Unable to authenticate (500)", Toast.LENGTH_LONG).show();
                            SaveSharedPreference.setAuthorizationIssue(context, true);
                            SaveSharedPreference.setAuthorizationIssueDescription(context, "Unable to authenticate (500)");
                            break;
                        default:
                            Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate (UNKNOWN)");
                            Toast.makeText(context, "Unable to authenticate (UNKNOWN)", Toast.LENGTH_LONG).show();
                            SaveSharedPreference.setAuthorizationIssue(context, true);
                            SaveSharedPreference.setAuthorizationIssueDescription(context, "Unable to authenticate (UNKNOWN)");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate: "+t.getMessage());
                Log.e("appMedici", "["+this.getClass().getSimpleName()+"] Unable to authenticate: "+t.getStackTrace());
                SaveSharedPreference.setAuthorizationIssue(context, true);
                SaveSharedPreference.setAuthorizationIssueDescription(context, "Unable to authenticate ("+t.getMessage()+")");
            }
        });
    }

}
