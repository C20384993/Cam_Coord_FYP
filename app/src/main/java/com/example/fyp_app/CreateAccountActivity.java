package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import clients.AccountAPIClient;
import models.Account;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    EditText editTextRegUsername;
    EditText editTextRegPassword;
    EditText editTextRegPassConfirm;
    Button btnRegAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editTextRegUsername = findViewById(R.id.editText_registerUsername);
        editTextRegPassword = findViewById(R.id.editText_registerPassword);
        editTextRegPassConfirm = findViewById(R.id.editText_registerPassConf);
        btnRegAccount = findViewById(R.id.btn_createAccount);

        editTextRegUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextRegUsername.getText().toString().trim().equals("") & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("")){
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
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
                if(!editTextRegUsername.getText().toString().trim().equals("") & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("") ){
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
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
                if(!editTextRegUsername.getText().toString().trim().equals("") & !editTextRegPassword.getText().toString().trim().equals("")
                        & !editTextRegPassConfirm.getText().toString().trim().equals("") ){
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnRegAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        btnRegAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }//end onCreate

    public void register(){
        String enteredUsername = editTextRegUsername.getText().toString();
        String enteredPassword = editTextRegPassword.getText().toString();
        String enteredConfirmPass = editTextRegPassConfirm.getText().toString();

        //Send POST request if  aren't empty.
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

        //Check passwords match.
        if(enteredPassword.equals(enteredConfirmPass) == false){
            editTextRegPassConfirm.setError("Passwords didn't match.");
            return;
        }

        //Else, send POST request.
        else{
            post(createAccountRequest(enteredUsername, enteredPassword));
        }//end else

    }//end register

    //Create a model containing the values to be put into the JSON object for the POST request.
    public Account createAccountRequest(String enteredUsername, String enteredPassword){
        Account accountRequest = new Account();
        accountRequest.setUsername(enteredUsername);
        accountRequest.setPassword(enteredPassword);
        return accountRequest;
    }

    //Send POST request containing the model.
    public void post(Account userRequest){
        Call<AccountResponse> userCall = AccountAPIClient.getUserService().sendAccount(userRequest);
        userCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Toast.makeText(CreateAccountActivity.this,
                            "Account created.",Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this,
                        "Could not connect to server.",Toast.LENGTH_LONG).show();
            }
        });
    }//end post
}