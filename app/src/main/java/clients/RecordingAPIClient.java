package clients;

import services.RecordingAPIService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordingAPIClient {
    private static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://c20384993fyp.uksouth.cloudapp.azure.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
    public static RecordingAPIService getRecordingService(){
        RecordingAPIService recordingAPIService = getRetrofit().create(RecordingAPIService.class);

        return recordingAPIService;
    }
}
