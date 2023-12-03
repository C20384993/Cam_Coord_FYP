package services;

import models.Account;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AccountAPIService {

    @GET
    Call<AccountResponse> getAccount(@Url String url);

    @POST("Accounts")
    Call<AccountResponse> sendAccount(@Body Account account);
}
