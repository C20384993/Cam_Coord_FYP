package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
public class AddCameraActivity extends AppCompatActivity {

    int cameraID;
    String cameraName;
    String camUsername;
    String camPassword;

    TextView addCamName;
    TextView addCamUsername;
    TextView addCamPassword;
    Button createCamBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        addCamName = findViewById(R.id.editText_AddCamName);
        addCamUsername = findViewById(R.id.editText_AddCamUsername);
        addCamPassword = findViewById(R.id.editText_AddCamPassword);
        createCamBtn = findViewById(R.id.button_CreateCamClass);
    }
}