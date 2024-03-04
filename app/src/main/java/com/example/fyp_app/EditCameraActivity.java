package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
    Button deleteCamBtn;


    String userid;
    String username;
    String password;
    String cameraid;
    String localIp;
    String streampath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_camera);

        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        cameraid = getIntent().getStringExtra("cameraid");
        localIp = getIntent().getStringExtra("rtspurl");
        streampath = getIntent().getStringExtra("streampath");

        edtTextCamName = findViewById(R.id.editText_EditCamName);
        edtTextCamUsername = findViewById(R.id.editText_EditCamUsername);
        edtTextCamPassword = findViewById(R.id.editText_EditCamPassword);
        edtTextRTSPURL = findViewById(R.id.editText_Editrtspurl);
        saveCamBtn = findViewById(R.id.button_SaveCamera);
        deleteCamBtn = findViewById(R.id.button_deleteCamera);

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

        deleteCamBtn.setOnClickListener(new View.OnClickListener() {
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
                deleteCamEntry(cameraid, userid);
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

        //Create the Camera object that will update the one in the database.
        CameraResponse cameraRequest = new CameraResponse();
        cameraRequest.setCameraid(Integer.parseInt(cameraid));
        cameraRequest.setCustomname(edtTextCamName.getText().toString());
        cameraRequest.setCamusername(edtTextCamUsername.getText().toString());
        cameraRequest.setCampassword(edtTextCamPassword.getText().toString());
        cameraRequest.setRtspurl(fullRtspUrl);
        cameraRequest.setStreampath(getIntent().getStringExtra("streampath"));
        cameraRequest.setUserid(Integer.parseInt(userid));

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
                intentCameraList.putExtra("username",username);
                intentCameraList.putExtra("password",password);
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

    public void deleteCamEntry(String cameraid, String userid){

        //Send the cameraRequest object.
        Call<Void> cameraCall = CameraAPIClient.getCameraService()
                .deleteCamera(cameraid);

        cameraCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(EditCameraActivity.this,
                        "Camera deleted.",Toast.LENGTH_LONG).show();

                Intent intentCamList = new Intent(EditCameraActivity.this,
                        CameraListActivity.class);

                intentCamList.putExtra("currentuserid",userid);
                intentCamList.putExtra("username",username);
                intentCamList.putExtra("password",password);
                finish();
                startActivity(intentCamList);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCameraActivity.this,
                        "Cam not deleted.",Toast.LENGTH_LONG).show();
            }
        });
    }//end deleteCamEntry

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}