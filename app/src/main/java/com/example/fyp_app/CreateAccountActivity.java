package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import clients.AccountAPIClient;
import models.Account;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//User can create an account from this activity.
public class CreateAccountActivity extends AppCompatActivity {

    //Layout items
    EditText editTextRegUsername;
    EditText editTextRegPassword;
    EditText editTextRegPassConfirm;
    Button buttonRegAccount;

    //Activity variables
    final private String restUrl = "https://c20384993fyp.uksouth.cloudapp.azure.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Locate items from layout
        editTextRegUsername = findViewById(R.id.editText_RegisterUsername);
        editTextRegPassword = findViewById(R.id.editText_RegisterPassword);
        editTextRegPassConfirm = findViewById(R.id.editText_RegisterPassConf);
        buttonRegAccount = findViewById(R.id.button_CreateAccount);

        //TextWatcher, tracks if the textfields are empty.
        //If all are empty, register button turns grey.
        editTextRegUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextRegUsername.getText().toString().trim().equals("")
                        & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("")){
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextRegPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextRegUsername.getText().toString().trim().equals("")
                        & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("") ){
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextRegPassConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextRegUsername.getText().toString().trim().equals("")
                        & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("") ){
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Attempt to create account using the entered details when pressed.
        buttonRegAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }//end onCreate

    //Retrieves the entered details,
    //Check that details are entered,
    //Check username isn't taken,
    //Then send request to create account.
    public void register(){
        //Retrieve user entered values from the text views.
        String enteredUsername = editTextRegUsername.getText().toString();
        String enteredPassword = editTextRegPassword.getText().toString();
        String enteredConfirmPass = editTextRegPassConfirm.getText().toString();

        //Send POST request if text views aren't empty.
        if(TextUtils.isEmpty(enteredUsername)){
            editTextRegUsername.setError("Please enter a username.");
            return;
        }//end if

        else if(TextUtils.isEmpty(enteredPassword)){
            editTextRegPassword.setError("Please enter a password.");
            return;
        }//end else if

        else if(TextUtils.isEmpty(enteredConfirmPass)){
            editTextRegPassConfirm.setError("Please confirm your password.");
            return;
        }//end else if

        //Check username and password are of minimum length.
        else if(enteredUsername.length() < 6){
            editTextRegUsername.setError("Username is too short.");
            return;
        }

        else if(enteredPassword.length() < 6){
            editTextRegPassword.setError("Password is too short.");
            return;
        }

        //Check passwords match.
        if(enteredPassword.equals(enteredConfirmPass) == false){
            editTextRegPassConfirm.setError("Passwords didn't match.");
            return;
        }

        //Check the username isn't taken.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .getAccount(restUrl +"/Accounts/getbyusername?username="+enteredUsername);

        userCall.enqueue(new Callback<AccountResponse>() {
            //If a reponse is gotten from userCall, the server is up.
            //If the response userid == 0, an account with that username doesn't exist.
            //If an account with the entered username is found, then don't allow it to be used.
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if(response.body().getUserid()==0){
                    post(createAccountRequest(enteredUsername, enteredPassword));
                }
                else {
                    Toast.makeText(CreateAccountActivity.this,
                            "Username already taken.", Toast.LENGTH_LONG).show();
                }
            }

            //Otherwise, create the account.
            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this,
                        "Server unavailable.", Toast.LENGTH_LONG).show();
            }
        });

    }//end register

    //Create a model containing the values to be put into the JSON object for the POST request.
    public Account createAccountRequest(String enteredUsername, String enteredPassword){
        Account accountRequest = new Account();
        //Create a salt value for the account.
        byte[] salt = generateSalt();
        accountRequest.setUsername(enteredUsername);
        accountRequest.setPassword(hashPassword(enteredPassword, salt));
        accountRequest.setSalt(Base64.getEncoder().encodeToString(salt));
        return accountRequest;
    }

    //Send POST request containing the model.
    public void post(Account userRequest){
        Call<AccountResponse> accountCreationCall = AccountAPIClient
                .getUserService().sendAccount(userRequest);
        accountCreationCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Toast.makeText(CreateAccountActivity.this,
                        "Account created.",Toast.LENGTH_LONG).show();
                //Finish activity and return to landing page.
                finish();
                startActivity(new Intent(CreateAccountActivity.this,
                        MainActivity.class));
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this,
                        "Could not connect to server.",Toast.LENGTH_LONG).show();
            }
        });
    }//end post

    public static String hashPassword(String password, byte[] salt) {
        try {
            //Create MessageDigest instance for SHA-512. Use salt value to increase hash length
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(salt);
            messageDigest.update(password.getBytes());
            byte[] hashedBytes = messageDigest.digest();

            //Combine the salt value to the hashed password to increase complexity.
            byte[] combined = new byte[salt.length + hashedBytes.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedBytes, 0, combined, salt.length, hashedBytes.length);

            //Convert bytes to Base64 format, allows it to be stored in Varchar.
            return Base64.getEncoder().encodeToString(combined);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }//end hashPassword

    private static byte[] generateSalt() {
        //Generate a salt value using the SecureRandom class.
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16]; //16 bytes = 128-bits, minimum value for decent security.
        secureRandom.nextBytes(salt);
        return salt;
    }//end generateSalt
}