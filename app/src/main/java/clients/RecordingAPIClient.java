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
    private static Retrofit getRetrofit(Context context) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(httpLoggingInterceptor);

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInputStream = context.getResources().openRawResource(R.raw.fullchain);
            Certificate certificate = cf.generateCertificate(certInputStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("certificate", certificate);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(),
                    (javax.net.ssl.X509TrustManager) trustManagerFactory.getTrustManagers()[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OkHttpClient okHttpClient = httpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://c20384993fyp.uksouth.cloudapp.azure.com") // Update with your Spring Boot app URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }
    public static RecordingAPIService getRecordingService(Context context){
        RecordingAPIService recordingAPIService = getRetrofit(context).create(RecordingAPIService.class);

        return recordingAPIService;
    }
}
