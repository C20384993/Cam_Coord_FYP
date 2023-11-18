package com.example.fyp_app;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface RecordingAPIService {
    @POST("Files")
    Call<RecordingResponse> sendRecording(@Body Recording recording);
}
