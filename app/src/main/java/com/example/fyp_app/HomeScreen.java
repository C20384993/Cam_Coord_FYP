package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

//This is the default activity after the user is signed in.
public class HomeScreen extends AppCompatActivity {

    //Layout items
    private Button buttonCameraList;
    private Button buttonRecordingsAndStreams;
    Toolbar toolbarAccountSettings;
    Button buttonDarkMode;

    //Activity Variables
    //Variables with the "current" prefix are where the details for the logged in user are tracked.
    String currentUserId;
    String currentUsername;
    String currentPassword;
    boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Retrieve the values for the currently logged in user.
        //Store them in the current-prefix variables.
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");

        //Locate items from layout
        buttonCameraList = findViewById(R.id.button_CameraList);
        buttonRecordingsAndStreams = findViewById(R.id.button_RecordingsAndStreams);
        buttonDarkMode = findViewById(R.id.button_DarkMode);
        toolbarAccountSettings = findViewById(R.id.account_Toolbar);
        setSupportActionBar(toolbarAccountSettings); //Displays 3 dots symbol for account/sign-out.

        //Open the CameraListActivity. It displays the cameras this account is managing/has added.
        buttonCameraList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCameraList = new Intent(HomeScreen.this,
                        CameraListActivity.class);

                //Pass the current user's details.
                intentCameraList.putExtra("currentuserid", currentUserId);
                intentCameraList.putExtra("username", currentUsername);
                intentCameraList.putExtra("password", currentPassword);
                startActivity(intentCameraList);
            }
        });

        //Open the menu for selecting the user's list of recordings or his list of streams.
        buttonRecordingsAndStreams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecOpt = new Intent(HomeScreen.this,
                        RecordingsAndStreamsPage.class);

                //Pass the current user's details.
                intentRecOpt.putExtra("currentuserid", currentUserId);
                intentRecOpt.putExtra("username", currentUsername);
                intentRecOpt.putExtra("password", currentPassword);
                startActivity(intentRecOpt);
            }
        });

        //Dark Mode checks.
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            darkMode = false;
            buttonDarkMode.setText("Dark mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.dark));
            toolbarAccountSettings.setBackgroundColor(getResources().getColor(R.color.white));
            toolbarAccountSettings.setTitleTextColor(getResources().getColor(R.color.grey));
        }
        else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
            buttonDarkMode.setText("Light mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.light_blue));
            toolbarAccountSettings.setBackgroundColor(getResources()
                    .getColor(R.color.darkBackground));
            toolbarAccountSettings.setTitleTextColor(getResources().getColor(R.color.grey));
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
                    toolbarAccountSettings.setBackgroundColor(getResources()
                            .getColor(R.color.white));
                    toolbarAccountSettings.setTitleTextColor(getResources().getColor(R.color.grey));
                    darkMode = false;
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    toolbarAccountSettings.setBackgroundColor(getResources()
                            .getColor(R.color.darkBackground));
                    toolbarAccountSettings.setTitleTextColor(getResources().getColor(R.color.grey));
                    darkMode=true;
                    buttonDarkMode.setText("Light mode");
                }
            }
        });

    }//end OnCreate

    //Alert dialogue, user must confirm he wants to sign out.
    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to sign out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intentEditAccount = new Intent(HomeScreen.this,
                        MainActivity.class);
                startActivity(intentEditAccount);
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

    //Open the 3-dots menu to either view account details or sign-out.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        return true;
    }

    //Functionality for homepage_menu options.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //Display the user's "My Account" page.
        if (id == R.id.myAccount){
            Intent intentEditAccount = new Intent(HomeScreen.this,
                    ViewAccountActivity.class);

            intentEditAccount.putExtra("currentuserid", currentUserId);
            intentEditAccount.putExtra("username", currentUsername);
            intentEditAccount.putExtra("password", currentPassword);
            startActivity(intentEditAccount);
        }
        //Return user to Main Activity (Landing page) and finish() to remove the current details.
        if(id == R.id.signOut){
            CreateAlertDialogue();
        }
        return true;
    }//end onOptionItemSelected

    //When back button is pressed, finish activity and return to previous one.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}//end Class