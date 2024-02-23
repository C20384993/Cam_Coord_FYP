package services;

import java.util.List;

import models.CameraRecyclerItem;
import models.RecordingRecyclerItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<List<RecordingRecyclerItem>> getRecordings(@Url String url);
    @GET()
    Call<List<CameraRecyclerItem>> getCameras(@Url String url);
}
