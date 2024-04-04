package clients;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.CameraAPIService;

public class CameraAPIClient {
    private static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://c20384993fyp.uksouth.cloudapp.azure.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static CameraAPIService getCameraService(){
        CameraAPIService cameraAPIService = getRetrofit().create(CameraAPIService.class);

        return cameraAPIService;
    }
}
