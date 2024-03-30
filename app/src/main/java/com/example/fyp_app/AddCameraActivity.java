package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

//Page for entering details of a new camera and adding it to the user's account.
public class AddCameraActivity extends AppCompatActivity {

    //Layout items
    TextView editTextCamName;
    TextView editTextCamUsername;
    TextView editTextCamPassword;
    TextView editTextRtspUrl;
    Button buttonCreateCamera;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        //Get intent values.
        currentUserId = getIntent().getStringExtra("userid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");

        //Locate items from layout.
        editTextCamName = findViewById(R.id.editText_AddCamName);
        editTextCamUsername = findViewById(R.id.editText_AddCamUsername);
        editTextCamPassword = findViewById(R.id.editText_AddCamPassword);
        editTextRtspUrl = findViewById(R.id.editText_rtspurl);
        buttonCreateCamera = findViewById(R.id.button_CreateCamClass);

        //TextWatchers track if the textfields are empty.
        //If all are empty, the button turns grey.
        editTextCamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextCamName.getText().toString().trim().equals("") &
                        !editTextCamUsername.getText().toString().trim().equals("")
                        & !editTextCamPassword.getText().toString().trim().equals("") &
                        !editTextRtspUrl.getText().toString().trim().equals("")){
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextCamUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextCamName.getText().toString().trim().equals("") &
                        !editTextCamUsername.getText().toString().trim().equals("")
                        & !editTextCamPassword.getText().toString().trim().equals("") &
                        !editTextRtspUrl.getText().toString().trim().equals("")){
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextCamPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextCamName.getText().toString().trim().equals("") &
                        !editTextCamUsername.getText().toString().trim().equals("")
                        & !editTextCamPassword.getText().toString().trim().equals("") &
                        !editTextRtspUrl.getText().toString().trim().equals("")){
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        editTextRtspUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextCamName.getText().toString().trim().equals("") &
                        !editTextCamUsername.getText().toString().trim().equals("")
                        & !editTextCamPassword.getText().toString().trim().equals("") &
                        !editTextRtspUrl.getText().toString().trim().equals("")){
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else {
                    buttonCreateCamera.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Get entered details from all the fields
        //Send a POST request to add a new camera to the user's account.
        buttonCreateCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCamera(currentUserId);
            }
        });
    }//end OnCreate


    //Alert dialogue, to remind user to run the Path Adder Tool after adding the camera.
    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Make sure to run the Cam-Coord Path Adder Tool after adding a camera.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddCameraActivity.this,
                        "Camera saved.",Toast.LENGTH_LONG).show();
                Intent intentCameraList = new Intent(AddCameraActivity.this,
                        CameraListActivity.class);

                intentCameraList.putExtra("currentuserid",currentUserId);
                intentCameraList.putExtra("username",currentUsername);
                intentCameraList.putExtra("password",currentPassword);
                finish();
                startActivity(intentCameraList);
            }
        });
        builder.create();
        builder.show();
    }//end CreateAlertDialogue

    
    public void AddCamera(String accountid){
        //Get custom camera name, username, and password from editText fields.
        String enteredCamName = editTextCamName.getText().toString();
        String enteredCamUsername = editTextCamUsername.getText().toString();
        String enteredCamPassword = editTextCamPassword.getText().toString();
        String enteredLocalIP = editTextRtspUrl.getText().toString();
        //Form the full RTSP URL from the camera's local IP, username, and password.
        String fullRtspUrl
                = "rtsp://"+enteredCamUsername+":"+enteredCamPassword+"@"+enteredLocalIP+":554";

        //Check fields aren't empty.
        if(enteredCamName.isEmpty()){
            editTextCamName.setError("Please enter a custom name for your camera");
            return;
        }
        if(enteredCamUsername.isEmpty()){
            editTextCamUsername.setError("Please enter the camera's RTSP username");
            return;
        }
        if(enteredCamPassword.isEmpty()){
            editTextCamPassword.setError("Please enter the camera's RTSP password");
            return;
        }
        if(enteredLocalIP.isEmpty()){
            editTextRtspUrl.setError("Please enter camera Local IP");
            return;
        }

        //Create the Camera object that will be recorded in the database.
        Camera cameraRequest = new Camera();
        cameraRequest.setCustomname(editTextCamName.getText().toString());
        cameraRequest.setCamusername(editTextCamUsername.getText().toString());
        cameraRequest.setCampassword(editTextCamPassword.getText().toString());
        cameraRequest.setRtspurl(fullRtspUrl);
        cameraRequest.setStreampath("https://172.166.189.197:8888/cam");
        cameraRequest.setUserid(Integer.parseInt(accountid));


        //Send the cameraRequest object.
        Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService(getApplicationContext())
                .sendCamera(cameraRequest);

        cameraCall.enqueue(new Callback<CameraResponse>() {
            @Override
            public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                //Immediately update streampath for the new camera by adding the camera id.
                CameraResponse cameraRequest = new CameraResponse();
                cameraRequest.setCameraid(response.body().getCameraid());
                cameraRequest.setCustomname(editTextCamName.getText().toString());
                cameraRequest.setCamusername(editTextCamUsername.getText().toString());
                cameraRequest.setCampassword(editTextCamPassword.getText().toString());
                cameraRequest.setRtspurl(fullRtspUrl);

                cameraRequest.setStreampath("https://172.166.189.197:8888/cam"+Integer
                        .toString(response.body().getCameraid())+"/index.m3u8");

                cameraRequest.setUserid(Integer.parseInt(accountid));

                //Send the cameraRequest object.
                Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService(getApplicationContext())
                        .updateCamera(cameraRequest);

                cameraCall.enqueue(new Callback<CameraResponse>() {
                    @Override
                    public void onResponse(Call<CameraResponse> call,
                                           Response<CameraResponse> response) {
                        //Remind user to run Path Adder Tool after adding a camera.
                        CreateAlertDialogue();

                    }

                    @Override
                    public void onFailure(Call<CameraResponse> call, Throwable t) {
                        Toast.makeText(AddCameraActivity.this,
                                "Server unavailable.",Toast.LENGTH_LONG).show();
                    }
                });
            }//end onResponse()

            @Override
            public void onFailure(Call<CameraResponse> call, Throwable t) {
                Toast.makeText(AddCameraActivity.this,
                        "Failed to save camera, server unavailable.",
                        Toast.LENGTH_LONG).show();
            }//end onFailure
        });
        
    }//end AddCamera

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentCamList = new Intent(AddCameraActivity.this,
                CameraListActivity.class);

        intentCamList.putExtra("currentuserid", currentUserId);
        intentCamList.putExtra("username", currentUsername);
        intentCamList.putExtra("password", currentPassword);
        this.finish();
        //Return to the Camera List.
        startActivity(intentCamList);
    }
}