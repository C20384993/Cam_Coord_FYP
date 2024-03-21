package clients;

import services.RecordingAPIService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordingAPIClient {

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

    public static RecordingAPIService getRecordingService(){
        RecordingAPIService recordingAPIService = getRetrofit().create(RecordingAPIService.class);

        return recordingAPIService;
    }
}
