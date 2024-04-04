package clients;

import android.content.Context;

import com.example.fyp_app.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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