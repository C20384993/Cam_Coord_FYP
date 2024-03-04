package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.File;
import java.sql.Blob;

import clients.CameraAPIClient;
import clients.RecordingAPIClient;
import models.CameraResponse;
import models.RecordingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRecordingActivity extends AppCompatActivity {

    TextView edtTextCustomName;
    TextView txtViewCreationDate;
    Button btnDownload;
    Button btnSave;
    Button btnDeleteRecording;
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    String userid;
    String username;
    String password;
    String recordingid;
    String cameraid;
    String customname;
    String relativefilepath;
    String creationdate;
    String originalCustname;

    //TODO: Fix creationdate textview not displaying.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);

        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        recordingid = getIntent().getStringExtra("recordingid");
        Log.e("AZURE","initialrecordingid = "+recordingid);
        cameraid = getIntent().getStringExtra("cameraid");
        customname = getIntent().getStringExtra("customname");
        relativefilepath = getIntent().getStringExtra("relativefilepath");
        creationdate = getIntent().getStringExtra("creationdate");
        originalCustname = customname;

        edtTextCustomName = findViewById(R.id.editText_recCustomName);
        txtViewCreationDate = findViewById(R.id.textView_creationDate);
        btnDownload = findViewById(R.id.button_recDownload);
        btnSave = findViewById(R.id.button_recSave);
        btnDeleteRecording = findViewById(R.id.btn_deleteRecording);

        edtTextCustomName.setText(getIntent().getStringExtra("customname"));
        txtViewCreationDate.setText(getIntent().getStringExtra("creationdate"));
        Log.e("AZURE","creationdate = "+creationdate);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadRecording(originalCustname);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(cameraid, userid, recordingid, relativefilepath);
            }
        });

        btnDeleteRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogue();
            }
        });
    }//end onCreate

    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this recording?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecording(originalCustname);
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

    public void saveChanges(String cameraid, String userid, String recordingid, String relativefilepath){
        //Get new custom cameraname, username, password, and IP, from editText fields.
        String enteredCustomName = edtTextCustomName.getText().toString();


        //Check fields aren't empty.
        if(enteredCustomName.isEmpty()){
            edtTextCustomName.setError("Please enter a custom name for your recording.");
            return;
        }

        //Create the Recording object that will be update the one in the database.
        RecordingResponse recordingRequest = new RecordingResponse();
        recordingRequest.setCustomname(edtTextCustomName.getText().toString());
        recordingRequest.setCreationdate(txtViewCreationDate.getText().toString());
        recordingRequest.setRelativefilepath(relativefilepath);
        recordingRequest.setUserid(Integer.parseInt(userid));
        recordingRequest.setRecordingid(Integer.parseInt(recordingid));
        recordingRequest.setCameraid(Integer.parseInt(cameraid));

        //Send the recordingRequest object.
        Call<RecordingResponse> recordingCall = RecordingAPIClient.getRecordingService()
                .updateRecording(recordingRequest);

        recordingCall.enqueue(new Callback<RecordingResponse>() {
            @Override
            public void onResponse(Call<RecordingResponse> call, Response<RecordingResponse> response) {
                Toast.makeText(EditRecordingActivity.this,
                        "Changes Saved.",Toast.LENGTH_LONG).show();
                Intent intentCameraList =
                        new Intent(EditRecordingActivity.this, RecordingListActivity.class);

                intentCameraList.putExtra("currentuserid",userid);
                startActivity(intentCameraList);
            }

            @Override
            public void onFailure(Call<RecordingResponse> call, Throwable t) {
                Toast.makeText(EditRecordingActivity.this,
                        "Changes not Saved.",Toast.LENGTH_LONG).show();
            }
        });
    }//end saveChanges

    public void downloadRecording(String originalCustomname){
        String userid = getIntent().getStringExtra("userid");
        //cant perform network tasks on main thread
            AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    // Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    // Create the blob client.
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    // Retrieve reference to a previously created container.
                    Log.e("AZURE","userid = "+userid);
                    Log.e("AZURE","originalCustomname = "+originalCustomname);

                    Log.e("AZURE","blobClient.getContainerReference = cont"+userid);
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+userid);
                    Log.e("AZURE","after the getContRef");

                    Log.e("AZURE","container.getBlockBlobReference = /"+originalCustomname);
                    CloudBlockBlob blob1 = container.getBlockBlobReference(originalCustomname);
                    Log.e("AZURE","after the getBlockBlobRef");

                    File file = new File("/storage/emulated/0/Download/" + originalCustomname);
                    blob1.downloadToFile("/storage/emulated/0/Download/" + originalCustomname);
                    success = true;
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Log.e("AZURE","upload failed: "+e);
                    success = false;
                }
                return success;
            }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    if (success) {
                        Toast.makeText(EditRecordingActivity.this, "Recording downloaded.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditRecordingActivity.this, "Failed to download recording.", Toast.LENGTH_LONG).show();
                    }
                }
        };

        task.execute();
    }//end downloadRecording

    public void deleteRecording(String originalCustomname){
        String userid = getIntent().getStringExtra("userid");
        //cant perform network tasks on main thread
        AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    // Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    // Create the blob client.
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    // Retrieve reference to a previously created container.
                    Log.e("AZURE","userid = "+userid);
                    Log.e("AZURE","originalCustomname = "+originalCustomname);

                    Log.e("AZURE","blobClient.getContainerReference = cont"+userid);
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+userid);
                    Log.e("AZURE","after the getContRef");

                    Log.e("AZURE","container.getBlockBlobReference = /"+originalCustomname);
                    CloudBlockBlob blob1 = container.getBlockBlobReference(originalCustomname);
                    Log.e("AZURE","after the getBlockBlobRef");

                    blob1.delete();
                    success = true;
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Log.e("AZURE","upload failed: "+e);
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    //Now delete from the database.
                    Call<Void> recordingCall = RecordingAPIClient.getRecordingService().deleteRecording(recordingid);
                    Log.e("AZURE","recordingid = "+recordingid);

                    recordingCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(EditRecordingActivity.this, "Recording deleted.", Toast.LENGTH_LONG).show();

                            Intent intentRecList = new Intent(EditRecordingActivity.this,
                                    RecordingListActivity.class);

                            intentRecList.putExtra("currentuserid",userid);
                            intentRecList.putExtra("username",username);
                            intentRecList.putExtra("password",password);
                            finish();
                            startActivity(intentRecList);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });


                } else {
                    Toast.makeText(EditRecordingActivity.this, "Failed to delete.", Toast.LENGTH_LONG).show();
                }
            }
        };

        task.execute();
    }//end downloadRecording

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}