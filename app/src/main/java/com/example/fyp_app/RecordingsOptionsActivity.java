package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RecordingsOptionsActivity extends AppCompatActivity {

    Button btnViewStream;
    Button btnViewRecordings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings_options);

        String userid = getIntent().getStringExtra("currentuserid");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        btnViewStream = findViewById(R.id.btnViewStream);
        btnViewRecordings = findViewById(R.id.btnViewRecordings);

        btnViewStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStreamList = new Intent(getApplicationContext(),
                        StreamListActivity.class);

                intentStreamList.putExtra("currentuserid",userid);
                intentStreamList.putExtra("username",userid);
                intentStreamList.putExtra("password",userid);
                startActivity(intentStreamList);
            }
        });

        btnViewRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecordingList = new Intent(getApplicationContext(),
                        RecordingListActivity.class);

                intentRecordingList.putExtra("currentuserid",userid);
                intentRecordingList.putExtra("username",userid);
                intentRecordingList.putExtra("password",userid);
                startActivity(intentRecordingList);
            }
        });
    }//end onCreate

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end Class