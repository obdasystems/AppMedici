package com.obdasystems.pocmedici.message.network;

import com.obdasystems.pocmedici.message.model.Message;
import com.obdasystems.pocmedici.message.model.OutMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MediciApiInterface {

    @GET("messages")
    Call<List<Message>> getInbox();

    @POST("messages")
    @FormUrlEncoded
    Call<Message> sendMessage(@Field("date") Long date,
                              @Field("text") String text,
                              @Field("subject") String subject,
                              @Field("adverseEvent") Boolean adverseEvent,
                              @Field("sender") String sender,
                              @Field("recipient") String recipient
                              );

}
