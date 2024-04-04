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

//User can change his account password from this activity.
public class EditAccountPasswordActivity extends AppCompatActivity {

    //Layout items
    TextView editTextOldPassword;
    TextView editTextEditPassword;
    TextView editTextConfirmPassword;
    Button buttonSaveAccount;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_password);

        //Locate items from layout
        editTextOldPassword = findViewById(R.id.editText_OldPassword);
        editTextEditPassword = findViewById(R.id.editText_NewPassword);
        editTextConfirmPassword = findViewById(R.id.editText_ConfirmPassword);
        buttonSaveAccount = findViewById(R.id.button_UpdatePassword);

        //Get intent values
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");

        //TextWatcher, tracks if the password textfields are empty.
        //If all are empty, the button turns grey.
        editTextOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextOldPassword.getText().toString().trim().equals("") &
                        !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
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
                if(!editTextOldPassword.getText().toString().trim().equals("") &
                        !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
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
                if(!editTextOldPassword.getText().toString().trim().equals("") &
                        !editTextEditPassword.getText().toString().trim().equals("")
                        & !editTextConfirmPassword.getText().toString().trim().equals("") ){
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Check the changes made are valid and update the user's account in the database.
        buttonSaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(currentUserId);
            }
        });
    }//end onCreate

    public void saveChanges(String accountId) {
        //Get old and new passwords from editText fields.
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

        //Check password isn't too short.
        else if(enteredPassword.length() < 6){
            editTextEditPassword.setError("Password is too short.");
            return;
        }

        //Check passwords match
        if(enteredOldPassword.equals(currentPassword) == false){
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
        if(enteredPassword.equals(currentPassword)){
            Toast.makeText(EditAccountPasswordActivity.this,
                    "Cannot use your current password.",Toast.LENGTH_LONG).show();
            return;
        }

        //Create the Account object that will update the one in the database.
        AccountResponse accountRequest = new AccountResponse();
        accountRequest.setUserid(Integer.parseInt(accountId));
        accountRequest.setUsername(currentUsername);
        accountRequest.setPassword(enteredPassword);

        //Make a PUT request to update the database Account table row.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .updateAccount(accountRequest);

        userCall.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {

                Toast.makeText(EditAccountPasswordActivity.this,
                        "Password updated.", Toast.LENGTH_LONG).show();

                //Start the "My Account" activity if password updated successfully.
                Intent intentViewAccount = new Intent(EditAccountPasswordActivity.this,
                        ViewAccountActivity.class);

                intentViewAccount.putExtra("currentuserid",accountId);
                intentViewAccount.putExtra("username", currentUsername);
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


    //Return to previous activity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentViewAccount = new Intent(EditAccountPasswordActivity.this,
                ViewAccountActivity.class);

        intentViewAccount.putExtra("currentuserid", currentUserId);
        intentViewAccount.putExtra("username", currentUsername);
        intentViewAccount.putExtra("password", currentPassword);
        this.finish();
        startActivity(intentViewAccount);
    }
}