package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//Provides two buttons/options: View List of camera streams, and View List of Recordings.
public class RecordingsAndStreamsPage extends AppCompatActivity {

    //Layout items
    Button buttonViewStreams;
    Button buttonViewRecordings;
    Button buttonDarkMode;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings_and_streams);

        //Get intent values.
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");

        //Locate items from layout.
        buttonViewStreams = findViewById(R.id.button_ViewStreams);
        buttonViewRecordings = findViewById(R.id.button_ViewRecordings);
        buttonDarkMode = findViewById(R.id.button_DarkMode);

        //Display a list of added camera video streams the user can view.
        buttonViewStreams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStreamList = new Intent(getApplicationContext(),
                        StreamListActivity.class);

                intentStreamList.putExtra("currentuserid", currentUserId);
                intentStreamList.putExtra("username", currentUsername);
                intentStreamList.putExtra("password", currentPassword);
                intentStreamList.putExtra("salt", currentSalt);
                startActivity(intentStreamList);
            }
        });

        //Display a list of recordings the user has made.
        buttonViewRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecordingList = new Intent(getApplicationContext(),
                        RecordingListActivity.class);

                intentRecordingList.putExtra("currentuserid", currentUserId);
                intentRecordingList.putExtra("username", currentUsername);
                intentRecordingList.putExtra("password", currentPassword);
                intentRecordingList.putExtra("salt", currentSalt);
                startActivity(intentRecordingList);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end Class