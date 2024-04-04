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
import models.CameraResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCameraActivity extends AppCompatActivity {

    //Layout items
    TextView editTextCamName;
    TextView editTextCamUsername;
    TextView editTextCamPassword;
    TextView editTextRtspUrl;
    Button buttonSaveCam;
    Button buttonDeleteCam;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String cameraId;
    String rtspUrl;
    String streamPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_camera);

        //Get intent values.
        currentUserId = getIntent().getStringExtra("userid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        cameraId = getIntent().getStringExtra("cameraid");
        rtspUrl = getIntent().getStringExtra("rtspurl");
        streamPath = getIntent().getStringExtra("streampath");

        //Locate items from layout.
        editTextCamName = findViewById(R.id.editText_EditCamName);
        editTextCamUsername = findViewById(R.id.editText_EditCamUsername);
        editTextCamPassword = findViewById(R.id.editText_EditCamPassword);
        editTextRtspUrl = findViewById(R.id.editText_EditRtspUrl);
        buttonSaveCam = findViewById(R.id.button_SaveCamera);
        buttonDeleteCam = findViewById(R.id.button_DeleteCamera);

        //Fill the text fields with the camera entry's details.
        editTextCamName.setText(getIntent().getStringExtra("customname"));
        editTextCamUsername.setText(getIntent().getStringExtra("camusername"));
        editTextCamPassword.setText(getIntent().getStringExtra("campassword"));

        //Only display the IP portion of the rtspUrl.
        rtspUrl = rtspUrl.substring(rtspUrl.indexOf("@") + 1);
        rtspUrl = rtspUrl.substring(0, rtspUrl.indexOf(":"));
        editTextRtspUrl.setText(rtspUrl);

        //TextWatcher, tracks if the textfields are empty.
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
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.grey));
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
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.grey));
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
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.grey));
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
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else{
                    buttonSaveCam.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Send PUT request to update entry details.
        buttonSaveCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(cameraId, currentUserId);
            }
        });

        //Send DELETE request to remove the camera from the database.
        //Creates an alert dialogue so user must confirm before deletion.
        buttonDeleteCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogue();
            }
        });
    }//end onCreate

    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this camera?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCamEntry(cameraId, currentUserId);
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

    //Get all of the values from the activity and send a PUT request to update the camera.
    public void saveChanges(String cameraid, String accountId){
        //Get new custom cameraname, username, password, and IP, from editText fields.
        String enteredCamName = editTextCamName.getText().toString();
        String enteredCamUsername = editTextCamUsername.getText().toString();
        String enteredCamPassword = editTextCamPassword.getText().toString();
        String enteredLocalIp = editTextRtspUrl.getText().toString();
        //Form the rtspUrl from the local IP, username, and password.
        String fullRtspUrl
                = "rtsp://"+enteredCamUsername+":"+enteredCamPassword+"@"+enteredLocalIp+":554";


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
        if(enteredLocalIp.isEmpty()){
            editTextCamPassword.setError("Please enter the camera's Local IP");
            return;
        }

        //Create the Camera object that will update the one in the database.
        CameraResponse cameraRequest = new CameraResponse();
        cameraRequest.setCameraid(Integer.parseInt(cameraid));
        cameraRequest.setCustomname(editTextCamName.getText().toString());
        cameraRequest.setCamusername(editTextCamUsername.getText().toString());
        cameraRequest.setCampassword(editTextCamPassword.getText().toString());
        cameraRequest.setRtspurl(fullRtspUrl);
        //Keep the streampath value the same, it only uses the cameraid which doesn't change.
        cameraRequest.setStreampath(getIntent().getStringExtra("streampath"));
        cameraRequest.setUserid(Integer.parseInt(accountId));

        //Send the cameraRequest object.
        Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService()
                .updateCamera(cameraRequest);

        cameraCall.enqueue(new Callback<CameraResponse>() {
            @Override
            public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                Toast.makeText(EditCameraActivity.this,
                        "Changes Saved.",Toast.LENGTH_LONG).show();

                //Changes were made successfully, so start the Camera List activity.
                Intent intentCameraList =
                        new Intent(EditCameraActivity.this, CameraListActivity.class);

                intentCameraList.putExtra("currentuserid",accountId);
                intentCameraList.putExtra("username", currentUsername);
                intentCameraList.putExtra("password", currentPassword);
                finish();
                startActivity(intentCameraList);
            }

            @Override
            public void onFailure(Call<CameraResponse> call, Throwable t) {
                Toast.makeText(EditCameraActivity.this,
                        "Changes not Saved.",Toast.LENGTH_LONG).show();
            }
        });
    }//end saveChanges

    //Find and delete the camera entry by its ID.
    public void deleteCamEntry(String cameraid, String accountId){

        Call<Void> cameraCall = CameraAPIClient.getCameraService()
                .deleteCamera(cameraid);

        cameraCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(EditCameraActivity.this,
                        "Camera deleted.",Toast.LENGTH_LONG).show();

                Intent intentCamList = new Intent(EditCameraActivity.this,
                        CameraListActivity.class);

                intentCamList.putExtra("currentuserid",accountId);
                intentCamList.putExtra("username", currentUsername);
                intentCamList.putExtra("password", currentPassword);
                finish();
                startActivity(intentCamList);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCameraActivity.this,
                        "Server unavailable, camera not deleted.",Toast.LENGTH_LONG).show();
            }
        });
    }//end deleteCamEntry

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentCamList = new Intent(EditCameraActivity.this,
                CameraListActivity.class);

        intentCamList.putExtra("currentuserid", currentUserId);
        intentCamList.putExtra("username", currentUsername);
        intentCamList.putExtra("password", currentPassword);
        this.finish();
        startActivity(intentCamList);
    }
}