package com.example.medicalchatbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/chat")
    Call<QueryResponse> sendQuery(@Body QueryRequest queryRequest);
}
