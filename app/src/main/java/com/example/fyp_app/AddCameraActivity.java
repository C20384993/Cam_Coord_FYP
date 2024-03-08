package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import clients.CameraAPIClient;
import models.Camera;
import models.CameraResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCameraActivity extends AppCompatActivity {
    final private String RESTURL = "http://192.168.68.131:8081";
    TextView edtTextCamName;
    TextView edtTextCamUsername;
    TextView edtTextCamPassword;
    TextView edtTextRTSPURL;
    Button createCamBtn;

    String userid;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        String userid = getIntent().getStringExtra("userid");
        edtTextCamName = findViewById(R.id.editText_AddCamName);
        edtTextCamUsername = findViewById(R.id.editText_AddCamUsername);
        edtTextCamPassword = findViewById(R.id.editText_AddCamPassword);
        edtTextRTSPURL = findViewById(R.id.editText_rtspurl);
        createCamBtn = findViewById(R.id.button_CreateCamClass);

        edtTextCamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextCamName.getText().toString().trim().equals("") & !edtTextCamUsername.getText().toString().trim().equals("")
                        & !edtTextCamPassword.getText().toString().trim().equals("") & !edtTextRTSPURL.getText().toString().trim().equals("")){
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        edtTextCamUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextCamName.getText().toString().trim().equals("") & !edtTextCamUsername.getText().toString().trim().equals("")
                        & !edtTextCamPassword.getText().toString().trim().equals("") & !edtTextRTSPURL.getText().toString().trim().equals("")){
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        edtTextCamPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextCamName.getText().toString().trim().equals("") & !edtTextCamUsername.getText().toString().trim().equals("")
                        & !edtTextCamPassword.getText().toString().trim().equals("") & !edtTextRTSPURL.getText().toString().trim().equals("")){
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        edtTextRTSPURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextCamName.getText().toString().trim().equals("") & !edtTextCamUsername.getText().toString().trim().equals("")
                        & !edtTextCamPassword.getText().toString().trim().equals("") & !edtTextRTSPURL.getText().toString().trim().equals("")){
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    createCamBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });


        createCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCamera(userid);
            }
        });


    }//end OnCreate

    
    public void AddCamera(String userid){
        //Get custom camera name, username, and password from editText fields.
        String enteredCamName = edtTextCamName.getText().toString();
        String enteredCamUsername = edtTextCamUsername.getText().toString();
        String enteredCamPassword = edtTextCamPassword.getText().toString();
        String enteredLocalIP = edtTextRTSPURL.getText().toString();
        String fullRtspUrl = "rtsp://"+enteredCamUsername+":"+enteredCamPassword+"@"+enteredLocalIP+":554";

        //Check fields aren't empty.
        if(enteredCamName.isEmpty()){
            edtTextCamName.setError("Please enter a custom name for your camera");
            return;
        }
        if(enteredCamUsername.isEmpty()){
            edtTextCamUsername.setError("Please enter the camera's RTSP username");
            return;
        }
        if(enteredCamPassword.isEmpty()){
            edtTextCamPassword.setError("Please enter the camera's RTSP password");
            return;
        }
        if(enteredLocalIP.isEmpty()){
            edtTextCamPassword.setError("Please enter camera Local IP");
            return;
        }

        //Create the Camera object that will be recorded in the database.
        Camera cameraRequest = new Camera();
        cameraRequest.setCustomname(edtTextCamName.getText().toString());
        cameraRequest.setCamusername(edtTextCamUsername.getText().toString());
        cameraRequest.setCampassword(edtTextCamPassword.getText().toString());
        cameraRequest.setRtspurl(fullRtspUrl);
        cameraRequest.setStreampath("https://172.166.189.197:8888/cam");
        cameraRequest.setUserid(Integer.parseInt(userid));


        //Send the cameraRequest object.
        Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService().sendCamera(cameraRequest);

        cameraCall.enqueue(new Callback<CameraResponse>() {
            @Override
            public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                //Immediately update steampath for new camera.
                CameraResponse cameraRequest = new CameraResponse();
                cameraRequest.setCameraid(response.body().getCameraid());
                cameraRequest.setCustomname(edtTextCamName.getText().toString());
                cameraRequest.setCamusername(edtTextCamUsername.getText().toString());
                cameraRequest.setCampassword(edtTextCamPassword.getText().toString());
                cameraRequest.setRtspurl(fullRtspUrl);
                cameraRequest.setStreampath("https://172.166.189.197:8888/cam"+Integer
                        .toString(response.body().getCameraid())+"/index.m3u8");
                cameraRequest.setUserid(Integer.parseInt(userid));

                //Send the cameraRequest object.
                Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService()
                        .updateCamera(cameraRequest);

                cameraCall.enqueue(new Callback<CameraResponse>() {
                    @Override
                    public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                        Toast.makeText(AddCameraActivity.this,
                                "Camera saved.",Toast.LENGTH_LONG).show();
                        Intent intentCameraList = new Intent(AddCameraActivity.this,
                                CameraListActivity.class);

                        intentCameraList.putExtra("currentuserid",userid);
                        finish();
                        startActivity(intentCameraList);
                    }

                    @Override
                    public void onFailure(Call<CameraResponse> call, Throwable t) {
                        Toast.makeText(AddCameraActivity.this,
                                "Error adding camera, check camera details.",Toast.LENGTH_LONG).show();
                    }
                });
            }//end onResponse()

            @Override
            public void onFailure(Call<CameraResponse> call, Throwable t) {
                Toast.makeText(AddCameraActivity.this,
                        "Camera save failed.",Toast.LENGTH_LONG).show();
            }//end onFailure
        });
        
    }//end AddCamera

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentCamList = new Intent(AddCameraActivity.this,
                CameraListActivity.class);

        intentCamList.putExtra("currentuserid",userid);
        intentCamList.putExtra("username",username);
        intentCamList.putExtra("password",password);
        this.finish();
        startActivity(intentCamList);
    }
}