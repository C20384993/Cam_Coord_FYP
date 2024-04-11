
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

//User can change his account username from this activity.
public class EditAccountUsernameActivity extends AppCompatActivity {

    //Layout items
    TextView editTextEditUsername;
    Button buttonSaveAccount;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    final private String restUrl = "https://c20384993fyp.uksouth.cloudapp.azure.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_username);

        //Locate items from layout
        editTextEditUsername = findViewById(R.id.editText_EditUsername);
        buttonSaveAccount = findViewById(R.id.button_SaveAccount);

        //Get intent values
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");

        //TextWatcher, tracks if the username textfield is empty.
        //If it is empty, the button turns grey.
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
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveAccount.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Attempt to save the new username for the account when this button is pressed.
        buttonSaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the new username,
                //Check it is not the same or taken,
                //Update the user's account.
                saveChanges(currentUserId);
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

        //Check fields aren't empty.
        if(enteredUsername.equals(currentUsername)) {
            editTextEditUsername.setError("Already using this username.");
            return;
        }//end if

        //Check username isn't too short.
        else if(enteredUsername.length() < 6){
            editTextEditUsername.setError("Username is too short.");
            return;
        }

        //Create the Account object that will update the one in the database.
        AccountResponse accountRequest = new AccountResponse();
        accountRequest.setUserid(Integer.parseInt(userid));
        accountRequest.setUsername(enteredUsername);
        accountRequest.setPassword(currentPassword);
        accountRequest.setSalt(currentSalt);

        //Check username isn't already being used.
        Call<AccountResponse> userCall = AccountAPIClient.getUserService()
                .getAccount(restUrl +"/Accounts/getbyusername?username="+enteredUsername);

        userCall.enqueue(new Callback<AccountResponse>() {
            //If a reponse is gotten from userCall, the server is up.
            //If the response.userid == 0, an account with that username doesn't exist.
            //If an account with the entered username is found, then don't allow it to be used.
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if(response.body().getUserid()==0){
                    Call<AccountResponse> usernameUpdateCall = AccountAPIClient.getUserService()
                            .updateAccount(accountRequest);

                    usernameUpdateCall.enqueue(new Callback<AccountResponse>() {
                        @Override
                        public void onResponse(Call<AccountResponse> call,
                                               Response<AccountResponse> response) {

                            Toast.makeText(EditAccountUsernameActivity.this,
                                    "Username updated.", Toast.LENGTH_LONG).show();

                            currentUsername = response.body().getUsername();
                            //Return user to My Account page.
                            Intent intentViewAccount = new Intent(
                                    EditAccountUsernameActivity.this,
                                    ViewAccountActivity.class);

                            intentViewAccount.putExtra("currentuserid",userid);
                            intentViewAccount.putExtra("username", currentUsername);
                            intentViewAccount.putExtra("password", currentPassword);
                            intentViewAccount.putExtra("salt", currentSalt);
                            finish();
                            startActivity(intentViewAccount);
                        }

                        @Override
                        public void onFailure(Call<AccountResponse> call, Throwable t) {
                            Toast.makeText(EditAccountUsernameActivity.this,
                                    "Username failed to update.",Toast.LENGTH_LONG).show();
                        }
                    });

                }//end if
                else {
                    Toast.makeText(EditAccountUsernameActivity.this,
                            "Username is taken.",Toast.LENGTH_LONG).show();
                }
            }

            //Otherwise, update the username.
            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Toast.makeText(EditAccountUsernameActivity.this,
                        "Server unavailable.",Toast.LENGTH_LONG).show();
            }
        });

    }//end saveChanges

    //Finish and return to previous activity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentViewAccount = new Intent(EditAccountUsernameActivity.this,
                ViewAccountActivity.class);

        intentViewAccount.putExtra("currentuserid", currentUserId);
        intentViewAccount.putExtra("username", currentUsername);
        intentViewAccount.putExtra("password", currentPassword);
        intentViewAccount.putExtra("salt", currentSalt);
        this.finish();
        startActivity(intentViewAccount);
    }
}
