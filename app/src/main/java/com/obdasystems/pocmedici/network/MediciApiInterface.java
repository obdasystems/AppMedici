package com.obdasystems.pocmedici.network;

import com.obdasystems.pocmedici.message.model.Message;
import com.obdasystems.pocmedici.network.request.LoginResponse;
import com.obdasystems.pocmedici.network.request.UserDeviceRegistrationRequest;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MediciApiInterface {

    @POST("auth/login")
    @FormUrlEncoded
    Call<LoginResponse> requestAuthentication(@Field("password") String pwd, @Field("username") String usrname);

    /**********************
            FORMS
     ***********************/

    @GET("forms")
    Call<List<RestForm>> getQuestionnaires();

    @POST("forms/{formId}/submit")
    Call<String> sendFilledForm(@Path("formId") int formId, @Body RestFilledForm filledForm);

    /**********************
             Messages
     ***********************/

    @GET("messages")
    Call<List<Message>> getInbox();

    @POST("messages")
    @FormUrlEncoded
    Call<Message> sendMessage(@Field("date") Long date,
                              @Field("text") String text,
                              @Field("subject") String subject,
                              @Field("adverseEvent") Boolean adverseEvent,
                              @Field("sender") String sender,
                              @Field("recipient") String recipient);


    /**********************
             Sensors
     ***********************/

    @POST("/devices/measures/gps")
    @FormUrlEncoded
    Call<String> sendPosition(@Field("date") long timestamp,
                              @Field("type") String type,
                              @Field("geometry") String position);

    @POST("/devices/measures/pedometer")
    @FormUrlEncoded
    Call<String> sendStepCount(@Field("date") long timestamp,
                               @Field("type") String type,
                               @Field("count") int stepCount);

    @POST("/users/devices/register")
    Call<JSONObject> registerInstanceId(@Body UserDeviceRegistrationRequest request);

    @POST("/users/devices/unregister")
    Call<JSONObject> unregisterInstanceId(@Body UserDeviceRegistrationRequest request);

}
