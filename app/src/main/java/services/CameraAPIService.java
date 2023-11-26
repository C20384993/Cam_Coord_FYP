package services;

import models.Camera;
import models.CameraResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CameraAPIService {
    @POST("Cameras")
    Call<CameraResponse> sendCamera(@Body Camera camera);
}
