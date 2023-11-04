package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

//TODO: Add user/account system.
//TODO: Retrieve list of cameras associated with user for recycler view from SQL database.
//TODO: Add recycler view of cameras.
public class CameraListActivity extends AppCompatActivity {

    FloatingActionButton addCameraBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);

        addCameraBtn = findViewById(R.id.addCameraBtn);
        addCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraListActivity.this, AddCameraActivity.class));
            }
        });
    }
}