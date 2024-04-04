package clients;

import android.content.Context;

import com.example.fyp_app.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import services.RecordingAPIService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
