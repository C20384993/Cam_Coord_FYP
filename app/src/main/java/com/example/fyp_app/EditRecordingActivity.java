package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    Button btnDownload;
    Button btnDeleteRecording;
    Button btnSaveRecording;
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
    String newCustomName;
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.

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
        btnDownload = findViewById(R.id.button_recDownload);
        btnDeleteRecording = findViewById(R.id.btn_deleteRecording);
        btnSaveRecording = findViewById(R.id.button_saveRecording);

        edtTextCustomName.setText(getIntent().getStringExtra("customname")
                .substring(0, getIntent().getStringExtra("customname").length() - 4));
        Log.e("AZURE","creationdate = "+creationdate);

        edtTextCustomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtTextCustomName.getText().toString().trim().equals("")){
                    btnSaveRecording.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else if(edtTextCustomName.getText().toString().equals(originalCustname)){
                    btnSaveRecording.setBackgroundColor(getResources().getColor(R.color.grey));
                }
                else{
                    btnSaveRecording.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });



        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadRecording(originalCustname);
            }
        });

        btnDeleteRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogue();
            }
        });

        btnSaveRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCustomName = edtTextCustomName.getText().toString()+".mkv";
                Log.e("AZURE","newCustomName = "+newCustomName);
                RenameRecording(originalCustname, newCustomName, edtTextCustomName.getText().toString());
            }
        });
    }//end onCreate

    private void RenameRecording(String originalCustomname, String newRecordingName, String newNameNoExtension) {

        if(TextUtils.isEmpty(newNameNoExtension)){
            edtTextCustomName.setError("Enter a name for the file.");
            return;
        }//end if

        if(originalCustomname.equals(newRecordingName)){
            edtTextCustomName.setError("File already has this name.");
            return;
        }

        //Download, Delete, Upload
        String userid = getIntent().getStringExtra("userid");
        Log.d("AZURE","newRecordingName = "+newRecordingName);

        //Download
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
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+userid);

                    CloudBlockBlob blob = container.getBlockBlobReference(originalCustomname);

                    File file = new File("/storage/emulated/0/Download/" + originalCustomname);
                    blob.downloadToFile("/storage/emulated/0/Download/" + originalCustomname);

                    //Downloaded, now delete from cloud.
                    blob.delete();

                    //Rename file + upload
                    File fileNewName = new File("/storage/emulated/0/Download/" + newRecordingName);
                    file.renameTo(fileNewName);

                    // Create the blob client.
                    CloudBlobClient blobClientRename = storageAccount.createCloudBlobClient();

                    // Retrieve reference to a previously created container.
                    CloudBlobContainer containerRename = blobClientRename.getContainerReference("cont"+userid);

                    //create blob if it doesn't exist - hopefully resolves bugs
                    containerRename.createIfNotExists();

                    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
                    CloudBlockBlob blobRename = container.getBlockBlobReference(newRecordingName);

                    blobRename.uploadFromFile(directory.getAbsolutePath()+"/"+newRecordingName);
                    Log.d("AZURE","upload function completed");

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
                    Toast.makeText(EditRecordingActivity.this, "Recording renamed.", Toast.LENGTH_LONG).show();
                    //Update DB
                    //Create the Recording object that will be update the one in the database.
                    RecordingResponse recordingRequest = new RecordingResponse();
                    recordingRequest.setCustomname(newRecordingName);
                    recordingRequest.setCreationdate(creationdate);
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
                            Intent intentRecList =
                                    new Intent(EditRecordingActivity.this, RecordingListActivity.class);

                            intentRecList.putExtra("currentuserid",userid);
                            intentRecList.putExtra("username",username);
                            intentRecList.putExtra("password",password);
                            finish();
                            startActivity(intentRecList);
                        }

                        @Override
                        public void onFailure(Call<RecordingResponse> call, Throwable t) {
                            Toast.makeText(EditRecordingActivity.this,
                                    "Changes not Saved.",Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(EditRecordingActivity.this, "Server unavailable.", Toast.LENGTH_LONG).show();
                }
            }
        };

        task.execute();

    }//end RenameRecording

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
        Intent intentRecList = new Intent(EditRecordingActivity.this,
                RecordingListActivity.class);

        intentRecList.putExtra("currentuserid",userid);
        intentRecList.putExtra("username",username);
        intentRecList.putExtra("password",password);
        this.finish();
        startActivity(intentRecList);
    }
}