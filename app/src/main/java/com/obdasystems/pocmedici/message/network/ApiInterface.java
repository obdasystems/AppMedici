package com.obdasystems.pocmedici.message.network;

import com.obdasystems.pocmedici.message.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("inbox.json")
    Call<List<Message>> getInbox();

}
