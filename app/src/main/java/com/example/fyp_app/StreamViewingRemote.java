package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import clients.RecordingAPIClient;
import models.Recording;
import models.RecordingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//View the RTSP stream from the HTTPS stream published by the MediaMTX server on the Azure VM.
public class StreamViewingRemote extends AppCompatActivity {

    //ExoPlayer object.
    private ExoPlayer player;

    //Layout items.
    Button buttonStartRecording;
    Button buttonSwitchSource;

    //Activity Variables
    String currentUserId;
    String cameraId;
    String rtspUrl;
    String streamPath;
    private boolean isRecording = false; //Tracks recording state.
    private boolean forcedClose = false; //Tracks if app was forcefully closed by user.
    private String outputFile; //The recording filename.
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_remote);

        //Locate items from layout.
        buttonStartRecording =findViewById(R.id.button_StartRecording);
        buttonSwitchSource =findViewById(R.id.button_SwitchSource);

        //Get intents.
        currentUserId = getIntent().getStringExtra("currentuserid");
        cameraId = getIntent().getStringExtra("cameraid");
        rtspUrl = getIntent().getStringExtra("rtspurl");
        streamPath = getIntent().getStringExtra("streampath");

        //Create a secure connection to the HLS stream.
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                // Retrieve the actual hostname from the SSL session
                String actualHostname = session.getPeerHost();

                // Perform hostname verification by comparing actual hostname with expected hostname
                return hostname.equalsIgnoreCase(actualHostname);
            }
        });

        //Create ExoPlayer
        StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(StreamViewingRemote.this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(streamPath);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        playerView.setUseController(false);

        //Button starts and stops recording.
        buttonStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording==false) {
                    startRecording();
                }//end if
                else if(isRecording==true){
                    forcedClose = false;
                    FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
                }
            }
        });

        //Switch between viewing the stream locally and remotely.
        buttonSwitchSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Play locally
                if(isRecording == false) {
                    Intent intentStreamViewing=
                            new Intent(StreamViewingRemote.this, StreamViewingLocal.class);
                    intentStreamViewing.putExtra("currentuserid",currentUserId);
                    intentStreamViewing.putExtra("cameraid", cameraId);
                    intentStreamViewing.putExtra("rtspurl",rtspUrl.toString());
                    intentStreamViewing.putExtra("streampath",streamPath);
                    finish();
                    startActivity(intentStreamViewing);
                }

                //Don't allow user to switch stream source if a recording is being made.
                else{
                    Toast.makeText(StreamViewingRemote.this,
                            "Can't switch while recording.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }//end onCreate

    //Inner class that creates the recording in the background, as FFmpeg commands block the
    //main thread.
    private class RecordTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(Void... params) {
            //Form the FFmpeg command used to record the RTSP stream
            String[] command = {"-y", "-i", rtspUrl.toString(),
                    "-acodec", "copy", "-vcodec", "copy", "-fflags", "nobuffer",
                    directory.getAbsolutePath()+"/"+outputFile.toString()};

            //Execute the FFmpeg command
            return FFmpeg.execute(command);

        }//end doInBackground

        @Override
        protected void onPostExecute(Integer result) {

            isRecording=false; //Update recording state.
            //Only add the recording to the database if recording stopped gracefully.
            if(forcedClose == false) {
                Post(createRecordingRequest(Integer.parseInt(currentUserId), cameraId)); //Send the newly created recording info.
            }
            buttonStartRecording.setText("Start rec");
            super.onPostExecute(result);
        }
    }//end RecordTask

    //Set the recording status to true and create the file and directory used to store the recording
    private void startRecording() {
        isRecording = true;
        buttonStartRecording.setText("Recording");

        //Select directory to save the recorded video to.
        if (!directory.exists()) {
            directory.mkdir();
        }

        //Generate a unique file name for each recording from the current time.
        //Uses .MKV, as not all cameras can save to .MP4.
        outputFile = "r_" + System.currentTimeMillis() + ".mkv";

        //Run the FFmpeg command in the background.
        new RecordTask().execute();
    }//end startRecording


    @Override
    protected void onStop()
    {
        if(isRecording==true){
            forcedClose = true;
            FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
        }
        File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
        recording.delete();
        super.onStop();
        player.stop();
        this.finish();
    }

    @Override
    protected void onPause() {
        if(isRecording==true){
            forcedClose = true;
            FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
        }
        File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
        recording.delete();
        super.onPause();
        player.stop();
        this.finish();
    }


    //Create the Recording object that will be recorded in the database.
    public Recording createRecordingRequest(int currentUserId, String cameraid){
        Recording recordingRequest = new Recording();
        recordingRequest.setCustomname(outputFile);
        recordingRequest.setUserid(currentUserId);
        recordingRequest.setRelativefilepath(directory.getAbsolutePath());
        recordingRequest.setCameraid(Integer.parseInt(cameraid));

        return recordingRequest;
    }//end createRecordingRequest

    //Send the recording details to the database.
    public void Post(Recording recordingRequest){
        Call<RecordingResponse> recordingCall = RecordingAPIClient.getRecordingService()
                .sendRecording(recordingRequest);
        recordingCall.enqueue(new Callback<RecordingResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecordingResponse> call,
                                   @NonNull Response<RecordingResponse> response) {

                Toast.makeText(StreamViewingRemote.this,
                        "Recording Saved.",Toast.LENGTH_LONG).show();
                //Upload
                uploadFile("./Internal storage/Download/",outputFile);

            }//end onResponse

            @Override
            public void onFailure(@NonNull Call<RecordingResponse> call, @NonNull Throwable t) {
                Toast.makeText(StreamViewingRemote.this,
                        "Failed to save recording.",Toast.LENGTH_LONG).show();
            }//end onFailure
        });
    }//end Post

    //Upload the recording to Azure Blob Storage. Create a container for the user and store the
    //recording and all future ones in his container.
    void uploadFile(String filePath, String fileName){
        String userid = getIntent().getStringExtra("currentuserid");

        //AsyncTask as it is not possible to perform network tasks on main thread
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    //Retrieve a storage account using the storageConnectionString.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    //Create the blob client
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    //Find a reference for an existing container.
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+userid);

                    //Create a blob if it doesn't already exist.
                    container.createIfNotExists();

                    //Create/Overwrite the blob with the recording that was made.
                    CloudBlockBlob blob = container.getBlockBlobReference(fileName);

                    blob.uploadFromFile(directory.getAbsolutePath()+"/"+fileName);

                    File recording = new File(directory.getAbsolutePath()+"/"+outputFile);

                    //Delete recording from phone once it is uploaded.
                    recording.delete();

                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Toast.makeText(StreamViewingRemote.this,
                            "Failed to upload recording.",Toast.LENGTH_LONG).show();
                }
                return null;
            }
        };
        task.execute();
    }//end uploadFile

    @Override
    public void onBackPressed() {
        if(isRecording==false) {
            player.release();
            File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
            recording.delete();
            super.onBackPressed();
            this.finish();
        }
        else{
            Toast.makeText(StreamViewingRemote.this,
                    "Can't exit while recording.",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        if(isRecording==true){
            forcedClose = true;
            FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
        }
        File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
        recording.delete();
        super.onDestroy();
        player.release();
        this.finish();
    }

}//end StreamViewingActivityRemote