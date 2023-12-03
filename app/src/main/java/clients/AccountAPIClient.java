package clients;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.AccountAPIService;

public class AccountAPIClient {
    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor).build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.68.131:8081")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;

    }

    public static AccountAPIService getUserService(){
        AccountAPIService accountAPIService = getRetrofit().create(AccountAPIService.class);

        return accountAPIService;
    }
}
