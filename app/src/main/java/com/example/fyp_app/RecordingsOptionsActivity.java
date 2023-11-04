package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RecordingsOptionsActivity extends AppCompatActivity {

    Button btnViewStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings_options);

        //TODO: Allow selecting of streams from RecyclerView list similar to CameraListActivity.
        btnViewStream = findViewById(R.id.btnViewStream);

        btnViewStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordingsOptionsActivity.this,
                        StreamViewingActivity.class));
            }
        });
    }
}