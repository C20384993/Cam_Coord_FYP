package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import clients.AccountAPIClient;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtTextAccountUsername;
    EditText edtTextAccountPassword;

    Button btnLogin;
    Button btnRegister;

    //TODO: Host REST API on Azure and connect to it from this line.
    //Change this to the IP of your local machine.
    final private String RESTURL = "http://192.168.68.131:8081";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTextAccountUsername = findViewById(R.id.editText_accountname);
        edtTextAccountPassword = findViewById(R.id.editText_accountpassword);
        btnLogin = findViewById(R.id.button_Login);
        btnRegister = findViewById(R.id.button_register);

        btnLogin.setOnClickListener(v -> {
            //Get username and password from editText field,
            //Check db for them,
            //If match is found, login with info.
            login();
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,
                    CreateAccountActivity.class));
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

                    //Check passwords match.
                    if(enteredPassword.equals(responsePassword)){
                        Intent intentHomeScreen = new Intent(MainActivity.this,
                                HomeScreen.class);

                        intentHomeScreen.putExtra("username",responseUsername);
                        //intentHomeScreen.putExtra("password",responsePassword);
                        intentHomeScreen.putExtra("currentuserid",String.valueOf(responseUserid));
                        startActivity(intentHomeScreen);
                    }
                    else{
                        Toast.makeText(MainActivity.this,
                                "Incorrect password.",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(MainActivity.this,
                            "Invalid username",Toast.LENGTH_LONG).show();

                }
            }


            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this,
                        "GET failed."+t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }//end login
}