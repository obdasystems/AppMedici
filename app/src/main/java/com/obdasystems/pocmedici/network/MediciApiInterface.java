package com.obdasystems.pocmedici.network;

import com.obdasystems.pocmedici.message.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MediciApiInterface {

    @POST("auth/login")
    @FormUrlEncoded
    Call<String> requestAuthentication(@Field("password") String pwd, @Field("username") String usrname);

    @GET("forms")
    Call<List<RestForm>> getQuestionnaires();

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



}
