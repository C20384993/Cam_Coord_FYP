package services;

import models.User;
import models.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UserAPIService {

    @GET
    Call<UserResponse> getUser(@Url String url);

    @POST("Users")
    Call<UserResponse> sendUser(@Body User user);
}
