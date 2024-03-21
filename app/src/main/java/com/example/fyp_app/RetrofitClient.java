package com.example.fyp_app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.ApiInterface;

//Used for connections to the REST API.
public class RetrofitClient {
    private static final String BASE_URL = "http://172.166.189.197:8081/";
    private static Retrofit retrofit = null;

    public static ApiInterface getRetrofitClient(){

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
