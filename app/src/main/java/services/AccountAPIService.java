package services;

import models.Account;
import models.AccountResponse;
import models.CameraResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AccountAPIService {

    @GET
    Call<AccountResponse> getAccount(@Url String url);

    @POST("/Accounts/create")
    Call<AccountResponse> sendAccount(@Body Account account);

    @PUT("/Accounts/update")
    Call<AccountResponse> updateAccount(@Body AccountResponse account);

    @DELETE("/Accounts/delete")
    Call<Void> deleteAccount(@Query("userid") String userid);
}
