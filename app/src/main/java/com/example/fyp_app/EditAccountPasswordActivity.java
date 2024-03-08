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
import models.AccountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccountPasswordActivity extends AppCompatActivity {

    TextView editTextOldPassword;
    TextView editTextEditPassword;
    TextView editTextConfirmPassword;
    Button btnSaveAccount;

    String userid;
    String username;
    String password;

    final private String RESTURL = "http://192.168.68.131:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_password);

        editTextOldPassword = findViewById(R.id.editText_oldPassword);
        editTextEditPassword = findViewById(R.id.editText_newPassword);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        btnSaveAccount = findViewById(R.id.btn_updatePassword);

        userid = getIntent().getStringExtra("currentuserid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        editTextOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextOldPassword.getText().toString().trim().equals("") & !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextOldPassword.getText().toString().trim().equals("") & !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    btnSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextOldPassword.getText().toString().trim().equals("") & !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
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
        String enteredOldPassword = editTextOldPassword.getText().toString();
        String enteredPassword = editTextEditPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        //Check fields aren't empty.
        if(enteredOldPassword.isEmpty()) {
            editTextOldPassword.setError("Please enter your old password.");
            return;
        }//end if

        if(enteredPassword.isEmpty()) {
            editTextEditPassword.setError("Please enter a new password.");
            return;
        }//end if

        if(confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Please confirm your password.");
            return;
        }//end if

        //Check passwords match
        if(enteredOldPassword.equals(password) == false){
            Toast.makeText(EditAccountPasswordActivity.this,
                    "Old password incorrect.",Toast.LENGTH_LONG).show();
            return;
        }

        if(enteredPassword.equals(confirmPassword) == false){
            Toast.makeText(EditAccountPasswordActivity.this,
                    "Passwords didn't match.",Toast.LENGTH_LONG).show();
            return;
        }

        //Check password isn't the same as the current one.
        if(enteredPassword.equals(password)){
            Toast.makeText(EditAccountPasswordActivity.this,
                    "Cannot use your current password.",Toast.LENGTH_LONG).show();
            return;
        }

        //Create the Account object that will update the one in the database.
        AccountResponse accountRequest = new AccountResponse();
        accountRequest.setUserid(Integer.parseInt(userid));
        accountRequest.setUsername(username);
        accountRequest.setPassword(enteredPassword);


        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .updateAccount(accountRequest);

        userCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                Toast.makeText(EditAccountPasswordActivity.this,
                                "Password updated.", Toast.LENGTH_LONG).show();

                username = response.body().getUsername();
                Intent intentViewAccount = new Intent(EditAccountPasswordActivity.this,
                                ViewAccountActivity.class);

                intentViewAccount.putExtra("currentuserid",userid);
                intentViewAccount.putExtra("username",username);
                intentViewAccount.putExtra("password",enteredPassword);
                finish();
                startActivity(intentViewAccount);
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(EditAccountPasswordActivity.this,
                        "Password failed to update.",Toast.LENGTH_LONG).show();
            }
        });
    }//end saveChanges

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentViewAccount = new Intent(EditAccountPasswordActivity.this,
                ViewAccountActivity.class);

        intentViewAccount.putExtra("currentuserid",userid);
        intentViewAccount.putExtra("username",username);
        intentViewAccount.putExtra("password",password);
        this.finish();
        startActivity(intentViewAccount);
    }
}