package clients;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.CameraAPIService;

public class CameraAPIClient {
    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor).build();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.166.189.197:8081")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;

    }

    public static CameraAPIService getCameraService(){
        CameraAPIService cameraAPIService = getRetrofit().create(CameraAPIService.class);

        return cameraAPIService;
    }
}
