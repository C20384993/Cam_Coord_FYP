package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class EditCameraActivity extends AppCompatActivity {

    final private String RESTURL = "http://192.168.68.131:8081";
    TextView edtTextCamName;
    TextView edtTextCamUsername;
    TextView edtTextCamPassword;
    TextView edtTextRTSPURL;
    Button saveCamBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_camera);

        String userid = getIntent().getStringExtra("userid");
        String cameraid = getIntent().getStringExtra("cameraid");

        String localIp = getIntent().getStringExtra("rtspurl");
        String streampath = getIntent().getStringExtra("streampath");

        edtTextCamName = findViewById(R.id.editText_EditCamName);
        edtTextCamUsername = findViewById(R.id.editText_EditCamUsername);
        edtTextCamPassword = findViewById(R.id.editText_EditCamPassword);
        edtTextRTSPURL = findViewById(R.id.editText_Editrtspurl);
        saveCamBtn = findViewById(R.id.button_SaveCamera);

        edtTextCamName.setText(getIntent().getStringExtra("customname"));
        edtTextCamUsername.setText(getIntent().getStringExtra("camusername"));
        edtTextCamPassword.setText(getIntent().getStringExtra("campassword"));

        //Only display the IP portion of the RTSPURL
        localIp = localIp.substring(localIp.indexOf("@") + 1);
        localIp = localIp.substring(0, localIp.indexOf(":"));
        edtTextRTSPURL.setText(localIp);

        //TODO: Empty fields and db checks
        //TODO: Delete Camera option, can only be deleted though if there are not recordings associated
        //Send update request to change entry.
        saveCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(cameraid, userid);
            }
        });
    }//end onCreate

    public void saveChanges(String cameraid, String userid){
        //Get new custom cameraname, username, password, and IP, from editText fields.
        String enteredCamName = edtTextCamName.getText().toString();
        String enteredCamUsername = edtTextCamUsername.getText().toString();
        String enteredCamPassword = edtTextCamPassword.getText().toString();
        String enteredLocalIp = edtTextRTSPURL.getText().toString();
        String fullRtspUrl = "rtsp://"+enteredCamUsername+":"+enteredCamPassword+"@"+enteredLocalIp+":554";


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
        if(enteredLocalIp.isEmpty()){
            edtTextCamPassword.setError("Please enter the camera's Local IP");
            return;
        }

        //Create the Camera object that will be update the one in the database.
        CameraResponse cameraRequest = new CameraResponse();
        cameraRequest.setCameraid(Integer.parseInt(cameraid));
        cameraRequest.setCustomname(edtTextCamName.getText().toString());
        cameraRequest.setCamusername(edtTextCamUsername.getText().toString());
        cameraRequest.setCampassword(edtTextCamPassword.getText().toString());
        cameraRequest.setRtspurl(fullRtspUrl);
        cameraRequest.setStreampath(getIntent().getStringExtra("streampath"));
        cameraRequest.setUserid(Integer.parseInt(userid));

        //TODO: DB checks to ensure camera isn't already added.
        //Send the cameraRequest object.
        Call<CameraResponse> cameraCall = CameraAPIClient.getCameraService()
                .updateCamera(cameraRequest);

        cameraCall.enqueue(new Callback<CameraResponse>() {
            @Override
            public void onResponse(Call<CameraResponse> call, Response<CameraResponse> response) {
                Toast.makeText(EditCameraActivity.this,
                        "Changes Saved.",Toast.LENGTH_LONG).show();
                Intent intentCameraList =
                        new Intent(EditCameraActivity.this, CameraListActivity.class);

                intentCameraList.putExtra("currentuserid",userid);
                startActivity(intentCameraList);
            }

            @Override
            public void onFailure(Call<CameraResponse> call, Throwable t) {
                Toast.makeText(EditCameraActivity.this,
                        "Changes not Saved.",Toast.LENGTH_LONG).show();
            }
        });
    }//end saveChanges
}