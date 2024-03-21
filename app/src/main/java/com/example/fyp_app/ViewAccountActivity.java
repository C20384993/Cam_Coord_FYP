package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import clients.AccountAPIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//The "My Account" page of the app. User can edit his profile from this activity.
public class ViewAccountActivity extends AppCompatActivity {

    //Layout items
    TextView textViewAccountUsername;
    TextView textViewAccountPassword;
    TextView textViewRevealPass;
    Button buttonDeleteAccount;
    Button buttonEditUsername;
    Button buttonEditPassword;
    Button buttonDarkMode;

    //Acitvity variables
    boolean darkMode = false;
    String currentUserId;
    String currentUsername;
    String currentPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        //Locate items from layout
        buttonDeleteAccount = findViewById(R.id.button_DeleteAccount);
        buttonEditUsername = findViewById(R.id.button_EditUsername);
        buttonEditPassword = findViewById(R.id.button_EditPassword);
        textViewRevealPass = findViewById(R.id.textView_RevealPass);
        textViewAccountUsername = findViewById(R.id.textView_Username);
        textViewAccountPassword = findViewById(R.id.textView_Password);
        buttonDarkMode = findViewById(R.id.button_DarkMode);

        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");

        //Hide the user's password by showing an empty string by default.
        String hiddenPassword = "";

        //Display username
        textViewAccountUsername.setText("Username: "+ currentUsername);
        textViewAccountPassword.setText("Password: "+hiddenPassword);

        //Start the activity to edit the account username.
        buttonEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditUsername = new Intent(ViewAccountActivity.this,
                        EditAccountUsernameActivity.class);

                intentEditUsername.putExtra("currentuserid", currentUserId);
                intentEditUsername.putExtra("username", currentUsername);
                intentEditUsername.putExtra("password", currentPassword);
                finish();
                startActivity(intentEditUsername);
            }
        });

        //Start the activity to edit the account password.
        buttonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditPassword = new Intent(ViewAccountActivity.this,
                        EditAccountPasswordActivity.class);

                intentEditPassword.putExtra("currentuserid", currentUserId);
                intentEditPassword.putExtra("username", currentUsername);
                intentEditPassword.putExtra("password", currentPassword);
                finish();
                startActivity(intentEditPassword);
            }
        });

        //Delete the user's account. Provide a confirmation box before deletion.
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Provide a confirmation box before deletion.
                CreateAlertDialogue();
            }
        });

        //When clicked, show the password for the account in the password text view.
        textViewRevealPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewAccountPassword.getText().equals("Password: "+hiddenPassword)){
                    Log.e("AZURE","in if");
                    Log.e("AZURE","gettext = "+ textViewAccountPassword.getText());
                    textViewAccountPassword.setText("Password: "+ currentPassword);
                    textViewRevealPass.setText("Click to hide");
                }
                else{
                    Log.e("AZURE","in else");
                    Log.e("AZURE","gettext = "+ textViewAccountPassword.getText());
                    textViewAccountPassword.setText("Password: "+hiddenPassword);
                    textViewRevealPass.setText("Click to show");
                }
            }
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
    }//end onCreate

    //Alert dialogue, user must confirm before his account is deleted.
    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount(currentUserId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }//end CreateAlertDialogue

    //Call the REST API to delete the user's account row from the database.
    public void deleteAccount(String userid){

        //Delete function call.
        Call<Void> accountCall = AccountAPIClient.getUserService()
                .deleteAccount(userid);

        accountCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account deleted.",Toast.LENGTH_LONG).show();

                //Account deleted, now return to app landing page.
                Intent intentMainActivity = new Intent(ViewAccountActivity.this,
                        MainActivity.class);

                intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intentMainActivity);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account not deleted.",Toast.LENGTH_LONG).show();
            }
        });
    }//end deleteAccount


    //When back button is pressed, finish activity and return to previous one.
    @Override
    public void onBackPressed() {
        Intent intentHomeScreen = new Intent(ViewAccountActivity.this,
                HomeScreen.class);

        intentHomeScreen.putExtra("currentuserid", currentUserId);
        intentHomeScreen.putExtra("username", currentUsername);
        intentHomeScreen.putExtra("password", currentPassword);
        super.onBackPressed();
        this.finish();
        startActivity(intentHomeScreen);
    }
}