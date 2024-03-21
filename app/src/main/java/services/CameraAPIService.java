package services;

import models.Camera;
import models.CameraResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

//Define REST API paths.
public interface CameraAPIService {

    @GET("/json.json")
    Call<CameraResponse> getCamera(@Url String url);
    @Headers({"Accept: application/json"})
    @POST("/Cameras/create")
    Call<CameraResponse> sendCamera(@Body Camera camera);
    @PUT("/Cameras/update")
    Call<CameraResponse> updateCamera(@Body CameraResponse camera);
    @DELETE("/Cameras/delete")
    Call<Void> deleteCamera(@Query("cameraid") String cameraid);
}
