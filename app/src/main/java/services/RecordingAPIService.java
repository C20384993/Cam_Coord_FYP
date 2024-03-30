package services;

import models.RecordingResponse;

import models.Recording;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

//Define REST API paths.
public interface RecordingAPIService {

    @GET
    Call<RecordingResponse> getRecording(@Url String url);
    @POST("/Recordings/create")
    Call<RecordingResponse> sendRecording(@Body Recording recording);
    @PUT("/Recordings/update")
    Call<RecordingResponse> updateRecording(@Body RecordingResponse recordingResponse);
    @DELETE("/Recordings/delete")
    Call<Void> deleteRecording(@Query("recordingid") String recordingid);
}
