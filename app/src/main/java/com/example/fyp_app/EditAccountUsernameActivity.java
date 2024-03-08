package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import clients.AccountAPIClient;
import clients.CameraAPIClient;
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountUsernameActivity extends AppCompatActivity {

    TextView editTextEditUsername;
    Button btnSaveAccount;


    String userid;
    String username;
    String password;

    final private String RESTURL = "http://192.168.68.131:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_username);

        editTextEditUsername = findViewById(R.id.editText_editUsername);
        btnSaveAccount = findViewById(R.id.btn_saveAccount);

        userid = getIntent().getStringExtra("currentuserid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        editTextEditUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextEditUsername.getText().toString().trim().equals("")){
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        btnSaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(userid);
            }
        });

    }//end onCreate

    public void saveChanges(String userid) {
        //Get new username and password from editText fields.
        String enteredUsername = editTextEditUsername.getText().toString();

        //Check fields aren't empty.
        if(enteredUsername.isEmpty()) {
            editTextEditUsername.setError("Please enter a new username.");
            return;
        }//end if

        //Create the Account object that will be update the one in the database.
        AccountResponse accountRequest = new AccountResponse();
        accountRequest.setUserid(Integer.parseInt(userid));
        accountRequest.setUsername(enteredUsername);
        accountRequest.setPassword(password);

        //Check username isn't already being used.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .getAccount(RESTURL+"/Accounts/getbyusername?username="+enteredUsername);

        userCall.enqueue(new Callback<AccountResponse>() {
            @Override //If an account with the entered username is found, then don't allow it to be used.
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Toast.makeText(EditAccountUsernameActivity.this,
                        "Username already taken.",Toast.LENGTH_LONG).show();
            }

            @Override //Otherwise, update the username.
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Call<AccountResponse> userCall2 = AccountAPIClient.getUserService()
                        .updateAccount(accountRequest);

                userCall2.enqueue(new Callback<AccountResponse>() {
                    @Override
                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                        Toast.makeText(EditAccountUsernameActivity.this,
                                "Username updated.", Toast.LENGTH_LONG).show();

                        username = response.body().getUsername();
                        Intent intentViewAccount = new Intent(EditAccountUsernameActivity.this,
                                ViewAccountActivity.class);

                        intentViewAccount.putExtra("currentuserid",userid);
                        intentViewAccount.putExtra("username",username);
                        intentViewAccount.putExtra("password",password);
                        finish();
                        startActivity(intentViewAccount);
                    }

                    @Override
                    public void onFailure(Call<AccountResponse> call, Throwable t) {
                        Toast.makeText(EditAccountUsernameActivity.this,
                                "Username failed to update.",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }//end saveChanges

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentViewAccount = new Intent(EditAccountUsernameActivity.this,
                ViewAccountActivity.class);

        intentViewAccount.putExtra("currentuserid",userid);
        intentViewAccount.putExtra("username",username);
        intentViewAccount.putExtra("password",password);
        this.finish();
        startActivity(intentViewAccount);
    }
}