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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

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
    String currentSalt;

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
        currentSalt = getIntent().getStringExtra("salt");

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
        byte[] salt = generateSalt(); //Generate a new salt value for the account
        accountRequest.setUsername(currentUsername);
        accountRequest.setPassword(hashPassword(enteredPassword, salt));
        accountRequest.setSalt(Base64.getEncoder().encodeToString(salt));

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
                intentViewAccount.putExtra("salt",salt);
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