package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import clients.AccountAPIClient;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Landing page for the app. User can sign in or create an account.
public class MainActivity extends AppCompatActivity {

    //Layout items
    EditText editTextAccountUsername;   //Text box for entering username.
    EditText editTextAccountPassword;   //Text box for entering password.
    TextView textViewRegisterLink;  //Links to the Create Account page/activity.
    Button buttonLogin; //Attempt to login with entered username and password.
    Button buttonDarkMode;  //Switch dark mode on/off.

    //Activity variables
    boolean darkMode = false; //Track if darkMode is on/off.
    final private String restUrl = "http://172.166.189.197:8081"; //REST API URL.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Locate items from layout
        editTextAccountUsername = findViewById(R.id.editText_AccountName);
        editTextAccountPassword = findViewById(R.id.editText_AccountPassword);
        textViewRegisterLink = findViewById(R.id.textView_RegisterLink);
        buttonLogin = findViewById(R.id.button_Login);
        buttonDarkMode = findViewById(R.id.button_DarkMode);

        //TextWatcher, tracks if the username textfield is empty. If it is and/or the password field
        //is empty, the login button turns grey.
        editTextAccountUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Turn button blue if both text fields are filled in.
                if(!editTextAccountPassword.getText().toString().trim().equals("")
                        & !editTextAccountUsername.getText().toString().trim().equals("")){
                    buttonLogin.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                //Turn grey if one or both is empty.
                else{
                    buttonLogin.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Same function as Username TextWatcher, but for the password field.
        editTextAccountPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextAccountPassword.getText().toString().trim().equals("")
                        & !editTextAccountUsername.getText().toString().trim().equals("")){
                    buttonLogin.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonLogin.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //When user clicks on this link, bring him to the CreateAccountActivity to make an account.
        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        CreateAccountActivity.class));
            }
        });

        buttonLogin.setOnClickListener(v -> {
            //Get username and password from editText field,
            //Check db for them,
            //If match is found, login with info.
            login();
        });

        //Dark Mode checks.
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            darkMode = false;
            buttonDarkMode.setText("Dark mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.dark));
        }
        else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
            buttonDarkMode.setText("Light mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        buttonDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(darkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    buttonDarkMode.setText("Dark mode");
                    darkMode = false;
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkMode=true;
                    buttonDarkMode.setText("Light mode");
                }
            }
        });
    }//end OnCreate

    //login(): Checks for an account with matching details to the those the user has entered.
    //If an account is found, and if the details are entered correctly, the user is logged in.
    public void login(){
        //Get username and password from editText field,
        String enteredUsername = editTextAccountUsername.getText().toString();
        String enteredPassword = editTextAccountPassword.getText().toString();

        //Check username and password fields aren't empty.
        if(enteredUsername.isEmpty()){
            editTextAccountUsername.setError("Please enter a username");
            return;
        }
        if(enteredPassword.isEmpty()){
            editTextAccountPassword.setError("Please enter a password");
            return;
        }

        //Check database for username entered, GET request with entered username.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .getAccount(restUrl +"/Accounts/getbyusername?username="+enteredUsername);

        //Returns the Account object from the database as a response.
        userCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccountResponse> call,
                                   @NonNull Response<AccountResponse> response) {

                //If an account object was returned, then the connection was successful.
                if(response.isSuccessful()){
                    //Get the username, password, and ID of the returned account.
                    String responseUsername = response.body().getUsername();
                    String responsePassword = response.body().getPassword();
                    int responseUserid = response.body().getUserid();

                    //The REST API will return a userID of 0 if an account with the entered
                    //username doesn't exist.
                    if(responseUserid == 0){
                        Toast.makeText(MainActivity.this,
                                "Invalid username",Toast.LENGTH_LONG).show();
                        return;
                    }

                    //Check passwords match, and if they do then pass user details to the
                    //HomeScreen Activity.
                    if(enteredPassword.equals(responsePassword)){
                        Intent intentHomeScreen = new Intent(MainActivity.this,
                                HomeScreen.class);

                        intentHomeScreen.putExtra("username",responseUsername);
                        intentHomeScreen.putExtra("password",responsePassword);
                        intentHomeScreen.putExtra("currentuserid",
                                String.valueOf(responseUserid));
                        startActivity(intentHomeScreen);
                        finish();
                    }
                    //Inform user the password was incorrect.
                    else{
                        Toast.makeText(MainActivity.this,
                                "Incorrect password.",Toast.LENGTH_LONG).show();
                    }

                }
                //If no account was returned, connection failed.
                else{
                    Toast.makeText(MainActivity.this,
                            "Could not connect to server.",Toast.LENGTH_LONG).show();
                }
            }

            //If there was no response, the connection failed.
            @Override
            public void onFailure(@NonNull Call<AccountResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Could not connect to server.",Toast.LENGTH_LONG).show();
            }
        });
    }//end login
}