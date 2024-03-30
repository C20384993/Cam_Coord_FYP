package clients;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.ApiInterface;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RetrofitClient {
    private static final String BASE_URL = "https://172.166.189.197:443/";
    private static Retrofit retrofit = null;

    public static ApiInterface getRetrofitClient(InputStream certificateInputStream) {
        if (retrofit == null) {
            // Create an OkHttpClient builder
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            try {
                char[] password = "password".toCharArray();
                // Load certificate
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);

                // Create a KeyStore containing our trusted CAs
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(certificateInputStream, password);
                keyStore.setCertificateEntry("tomcat", certificate);

                // Create a TrustManager that trusts the CAs in our KeyStore
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                // Create an SSL context with our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

                // Set the custom SSL context to OkHttpClient
                httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add logging interceptor for debugging purposes
            //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            //httpClient.addInterceptor(logging);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String message) {
                    Log.d("OkHttp", message); // or Log.e() for errors
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }
}
