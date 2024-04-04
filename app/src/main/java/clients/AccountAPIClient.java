package clients;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.AccountAPIService;

public class AccountAPIClient {

    private static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://c20384993fyp.uksouth.cloudapp.azure.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static AccountAPIService getUserService() {
        AccountAPIService accountAPIService = getRetrofit().create(AccountAPIService.class);
        return accountAPIService;
    }
}