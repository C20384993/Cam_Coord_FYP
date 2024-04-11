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
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;

import clients.RecordingAPIClient;
import models.RecordingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Provides options to change the current recording name, play the recording, or delete it.
public class EditRecordingActivity extends AppCompatActivity {

    //Layout items.
    TextView editTextCustomName;
    Button buttonPlayRecording;
    Button buttonDeleteRecording;
    Button buttonSaveRecordingChanges;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    String currentRecordingId;
    String cameraId;
    String creationDate;
    String customName; //Used if the user wants to change the recording name.
    String originalCustomName; //The recording name initially.
    String relativeFilepath;
    String newCustomName;
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recording);

        //Get intent values.
        currentUserId = getIntent().getStringExtra("userid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");
        currentRecordingId = getIntent().getStringExtra("recordingid");
        cameraId = getIntent().getStringExtra("cameraid");
        customName = getIntent().getStringExtra("customname");
        relativeFilepath = getIntent().getStringExtra("relativefilepath");
        creationDate = getIntent().getStringExtra("creationDate");
        originalCustomName = customName;

        editTextCustomName = findViewById(R.id.editText_recCustomName);
        buttonPlayRecording = findViewById(R.id.button_PlayRecording);
        buttonDeleteRecording = findViewById(R.id.button_deleteRecording);
        buttonSaveRecordingChanges = findViewById(R.id.button_saveRecording);

        //Remove the .mkv file extension when displaying the recording name.
        editTextCustomName.setText(getIntent().getStringExtra("customname")
                .substring(0, getIntent().getStringExtra("customname").length() - 4));

        //TextWatcher, tracks if the textfields are empty.
        //If all are empty, the button turns grey.
        editTextCustomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!editTextCustomName.getText().toString().trim().equals("")){
                    buttonSaveRecordingChanges.setBackgroundColor(getResources().getColor(R.color.blue));
                }
                else if(editTextCustomName.getText().toString().equals(originalCustomName)){
                    buttonSaveRecordingChanges.setBackgroundColor(getResources().getColor(R.color.grey));
                }
                else{
                    buttonSaveRecordingChanges.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        //Play the recording in the app using ExoPlayer.
        buttonPlayRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Download and play the recording.
                playRecording(originalCustomName);
            }
        });

        //Delete the recording from the database and Azure Blob Storage.
        //Create an AlertDialogue so user must confirm the delete.
        buttonDeleteRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogue();
            }
        });

        //Check if the user has renamed the recording, then save the changes to the database and blob storage.
        buttonSaveRecordingChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCustomName = editTextCustomName.getText().toString()+".mkv";
                RenameRecording(originalCustomName, newCustomName,
                        editTextCustomName.getText().toString());
            }
        });
    }//end onCreate

    //To rename the recording, it must be:
    //Downloaded,
    //Deleted from the Blob Storage,
    //Reuploaded with the new name but the same details.
    private void RenameRecording(String originalCustomname, String newRecordingName,
                                 String newNameNoExtension) {

        //Check filename isn't empty.
        if(TextUtils.isEmpty(newNameNoExtension)){
            editTextCustomName.setError("Enter a name for the file.");
            return;
        }//end if

        //Check the same name isn't being used.
        if(originalCustomname.equals(newRecordingName)){
            editTextCustomName.setError("File already has this name.");
            return;
        }

        //Download, Delete, Upload
        //Download
        AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    //Retrieve a storage account using the storageConnectionString.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    //Create the blob client
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    //Find a reference for an existing container.
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+currentUserId);

                    //Retrieve the blob from the recording name
                    CloudBlockBlob blob = container.getBlockBlobReference(originalCustomname);

                    //Create and download the recording.
                    File file = new File("/storage/emulated/0/Download/" + originalCustomname);
                    blob.downloadToFile("/storage/emulated/0/Download/" + originalCustomname);

                    //Downloaded, now delete from cloud.
                    blob.delete();

                    //Rename file and upload
                    File fileNewName = new File("/storage/emulated/0/Download/" + newRecordingName);
                    file.renameTo(fileNewName);

                    //Create the blob client
                    CloudBlobClient blobClientRename = storageAccount.createCloudBlobClient();

                    //Find a reference for an existing container.
                    CloudBlobContainer containerRename = blobClientRename.getContainerReference("cont"+currentUserId);

                    //Create a blob if it doesn't already exist.
                    containerRename.createIfNotExists();

                    //Overwrite the blob with the recording's new name.
                    CloudBlockBlob blobRename = container.getBlockBlobReference(newRecordingName);

                    //Upload the renamed recording file.
                    blobRename.uploadFromFile(directory.getAbsolutePath()+"/"+newRecordingName);
                    File recording = new File(directory.getAbsolutePath()+"/"+newRecordingName);

                    //Delete the downloaded recording from the phone.
                    recording.delete();

                    //Recording was renamed in the blob storage, so continue with next part.
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
                    //Update the database.
                    //Create the Recording object that will be update the one in the database.
                    RecordingResponse recordingRequest = new RecordingResponse();
                    recordingRequest.setCustomname(newRecordingName);
                    recordingRequest.setCreationdate(creationDate);
                    recordingRequest.setRelativefilepath(relativeFilepath);
                    recordingRequest.setUserid(Integer.parseInt(currentUserId));
                    recordingRequest.setRecordingid(Integer.parseInt(currentRecordingId));
                    recordingRequest.setCameraid(Integer.parseInt(cameraId));

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

                            intentRecList.putExtra("currentuserid",currentUserId);
                            intentRecList.putExtra("username", currentUsername);
                            intentRecList.putExtra("password", currentPassword);
                            intentRecList.putExtra("salt", currentSalt);
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

    //User must confirm deletion of recording in the Alert Dialogue.
    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this recording?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRecording(originalCustomName);
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

    //Download the recording and play it using ExoPlayer.
    public void playRecording(String originalCustomname){
        String userid = getIntent().getStringExtra("userid");

        //AsyncTask as it is not possible to perform network tasks on main thread
            AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    //Retrieve a storage account using the storageConnectionString.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    //Create the blob client
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    //Find a reference for an existing container.
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+currentUserId);

                    //Retrieve the blob with the recording blob.
                    CloudBlockBlob playBlob = container.getBlockBlobReference(originalCustomname);

                    File file = new File("/storage/emulated/0/Download/" + originalCustomname);
                    playBlob.downloadToFile("/storage/emulated/0/Download/" + originalCustomname);
                    success = true;
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Toast.makeText(EditRecordingActivity.this, "Server Unavailable.", Toast.LENGTH_LONG).show();
                    success = false;
                }
                return success;
            }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    if (success) {
                        Toast.makeText(EditRecordingActivity.this, "Playing Recording.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditRecordingActivity.this, "Failed to get recording.", Toast.LENGTH_LONG).show();
                    }
                    Intent intentViewRecording =
                            new Intent(EditRecordingActivity.this, RecordingViewerActivity.class);

                    intentViewRecording.putExtra("userid",userid);
                    intentViewRecording.putExtra("username", currentUsername);
                    intentViewRecording.putExtra("password", currentPassword);
                    intentViewRecording.putExtra("salt", currentSalt);
                    intentViewRecording.putExtra("cameraid", cameraId);
                    intentViewRecording.putExtra("recordingname", originalCustomName);
                    intentViewRecording.putExtra("recordingid", currentRecordingId);
                    intentViewRecording.putExtra("relativefilepath", relativeFilepath);
                    intentViewRecording.putExtra("creationdate", creationDate);
                    Log.e("AZURE","OriginalCustomname: "+originalCustomname);
                    finish();
                    startActivity(intentViewRecording);
                }
        };

        task.execute();
    }//end playRecording

    //Delete the recording from the Blob Storage and the database.
    public void deleteRecording(String originalCustomname){

        //AsyncTask as it is not possible to perform network tasks on main thread
        AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    //Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    //Create the blob client.
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    //Retrieve reference to a previously created container.
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+currentUserId);

                    //Retrieve the blob with the recording blob.
                    CloudBlockBlob deleteBlob = container.getBlockBlobReference(originalCustomname);

                    deleteBlob.delete();
                    success = true;
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Toast.makeText(EditRecordingActivity.this, "Server Unavailable.", Toast.LENGTH_LONG).show();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    //Now delete from the database.
                    Call<Void> recordingCall = RecordingAPIClient.getRecordingService().deleteRecording(currentRecordingId);
                    Log.e("AZURE","recordingid = "+ currentRecordingId);

                    recordingCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(EditRecordingActivity.this, "Recording deleted.", Toast.LENGTH_LONG).show();

                            Intent intentRecList = new Intent(EditRecordingActivity.this,
                                    RecordingListActivity.class);

                            intentRecList.putExtra("currentuserid",currentUserId);
                            intentRecList.putExtra("username", currentUsername);
                            intentRecList.putExtra("password", currentPassword);
                            intentRecList.putExtra("salt", currentSalt);
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
    }//end deleteRecording

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentRecList = new Intent(EditRecordingActivity.this,
                RecordingListActivity.class);

        intentRecList.putExtra("currentuserid", currentUserId);
        intentRecList.putExtra("username", currentUsername);
        intentRecList.putExtra("password", currentPassword);
        intentRecList.putExtra("salt", currentSalt);
        this.finish();
        startActivity(intentRecList);
    }
}