package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import clients.AccountAPIClient;
import models.AccountResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtTextAccountUsername;
    EditText edtTextAccountPassword;
    TextView textViewRegisterLink;
    Button btnLogin;


    //final private String RESTURL = "http://172.166.189.197:8081";
    final private String RESTURL = "http://192.168.68.131:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTextAccountUsername = findViewById(R.id.editText_accountname);
        edtTextAccountPassword = findViewById(R.id.editText_accountpassword);
        textViewRegisterLink = findViewById(R.id.textView_registerTitle);
        btnLogin = findViewById(R.id.button_Login);

        edtTextAccountUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextAccountPassword.getText().toString().trim().equals("") & !edtTextAccountUsername.getText().toString().trim().equals("")){
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        edtTextAccountPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextAccountPassword.getText().toString().trim().equals("") & !edtTextAccountUsername.getText().toString().trim().equals("")){
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        CreateAccountActivity.class));
            }
        });

        btnLogin.setOnClickListener(v -> {
            //Get username and password from editText field,
            //Check db for them,
            //If match is found, login with info.
            login();
        });

    }//end OnCreate

    public void login(){
        //Get username and password from editText field,
        String enteredUsername = edtTextAccountUsername.getText().toString();
        String enteredPassword = edtTextAccountPassword.getText().toString();

        //Check username and password fields aren't empty.
        if(enteredUsername.isEmpty()){
            edtTextAccountUsername.setError("Please enter a username");
            return;
        }
        if(enteredPassword.isEmpty()){
            edtTextAccountPassword.setError("Please enter a password");
            return;
        }

        //Check db for username entered, GET request with entered username.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .getAccount(RESTURL+"/Accounts/getbyusername?username="+enteredUsername);

        userCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call,
                                   @NonNull Response<AccountResponse> response) {

                if(response.isSuccessful()){
                    String responseUsername = response.body().getUsername();
                    String responsePassword = response.body().getPassword();
                    int responseUserid = response.body().getUserid();

                    //Check username
                    if(responseUserid == 0){
                        Toast.makeText(MainActivity.this,
                                "Invalid username",Toast.LENGTH_LONG).show();
                        return;
                    }

                    //Check passwords match.
                    if(enteredPassword.equals(responsePassword)){
                        Intent intentHomeScreen = new Intent(MainActivity.this,
                                HomeScreen.class);

                        intentHomeScreen.putExtra("username",responseUsername);
                        intentHomeScreen.putExtra("password",responsePassword);
                        intentHomeScreen.putExtra("currentuserid",String.valueOf(responseUserid));
                        startActivity(intentHomeScreen);
                        finish();
                    }
                    else{
                        Toast.makeText(MainActivity.this,
                                "Incorrect password.",Toast.LENGTH_LONG).show();
                    }

                }
            }


            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Could not connect to server.",Toast.LENGTH_LONG).show();
            }
        });

    }//end login
}