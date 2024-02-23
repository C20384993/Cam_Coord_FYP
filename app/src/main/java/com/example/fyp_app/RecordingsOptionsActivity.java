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
        //TODO: Allow selecting of streams from RecyclerView list similar to CameraListActivity.
        btnViewStream = findViewById(R.id.btnViewStream);
        btnViewRecordings = findViewById(R.id.btnViewRecordings);

        btnViewStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStreamList = new Intent(getApplicationContext(),
                        StreamListActivity.class);

                intentStreamList.putExtra("currentuserid",userid);
                startActivity(intentStreamList);
            }
        });

        btnViewRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecordingList = new Intent(getApplicationContext(),
                        RecordingListActivity.class);

                intentRecordingList.putExtra("currentuserid",userid);
                startActivity(intentRecordingList);
            }
        });
    }//end onCreate
}//end Class