package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    private Button btnSensorManage;
    private Button btnRecordings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Intent signInIntent = getIntent();
        //signInIntent.getStringExtra("username");

        btnSensorManage = findViewById(R.id.btnMainActivity_SensorManage);
        btnRecordings = findViewById(R.id.btnMainActivity_Recordings);

        btnSensorManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, CameraListActivity.class));
            }
        });

        btnRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this,
                        RecordingsOptionsActivity.class));
            }
        });

    }
}