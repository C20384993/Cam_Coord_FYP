package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.slider.Slider;
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


public class StreamViewingActivityRemote extends AppCompatActivity {

    //ExoPlayer
    private ExoPlayer player;
    Button recordButton;
    Button switchButton;
    Button recordLengthButton;
    Slider slider;



    String currentUserId;
    String cameraid;
    String rtspUrl;
    String streamPath;
    private boolean isRecording = false; //Tracks recording state.
    private String outputFile; //The recording filename.
    String timeRange = "10"; //How long the recording will be for. Starter values: 10 seconds, 20s , 30s.
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_viewing_remote);
        recordButton=findViewById(R.id.btn_record);
        switchButton=findViewById(R.id.btn_switch);

        recordLengthButton=findViewById(R.id.btn_record_length);
        recordLengthButton.setClickable(false);

        slider=findViewById(R.id.time_slider);
        slider.addOnSliderTouchListener(touchListener);

        //Get intents.
        currentUserId = getIntent().getStringExtra("currentuserid");
        cameraid = getIntent().getStringExtra("cameraid");
        rtspUrl = getIntent().getStringExtra("rtspurl");
        streamPath = getIntent().getStringExtra("streampath");
        Log.e("AZURE","streamPath = "+streamPath);

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                // Retrieve the actual hostname from the SSL session
                String actualHostname = session.getPeerHost();
                Log.e("AZURE","actualHostname = "+actualHostname);
                Log.e("AZURE","hostname = "+hostname);

                // Perform hostname verification by comparing actual hostname with expected hostname
                return hostname.equalsIgnoreCase(actualHostname);
            }
        });

        //Create ExoPlayer
        StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(StreamViewingActivityRemote.this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(streamPath);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        playerView.setUseController(false);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording==false) {
                    isRecording=true;
                    Log.e("AZURE","isRecording in onClick = "+isRecording);
                    startRecording();
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Play locally
                if(isRecording == false) {
                    Intent intentStreamViewing=
                            new Intent(StreamViewingActivityRemote.this, StreamViewingActivity.class);
                    intentStreamViewing.putExtra("currentuserid",currentUserId);
                    intentStreamViewing.putExtra("cameraid",cameraid);
                    intentStreamViewing.putExtra("rtspurl",rtspUrl.toString());
                    intentStreamViewing.putExtra("streampath",streamPath);
                    finish();
                    startActivity(intentStreamViewing);
                }

                else{
                    Toast.makeText(StreamViewingActivityRemote.this,
                            "Can't switch while recording.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }//end onCreate

    private final Slider.OnSliderTouchListener touchListener =
            new Slider.OnSliderTouchListener() {

                @Override
                public void onStartTrackingTouch(Slider slider) {
                    timeRange = String.valueOf(slider.getValue());
                    recordLengthButton.setText("Record for (s): "+timeRange+" seconds");
                    Log.e("AZURE", "timeRange slider = " + timeRange);

                }

                @Override
                public void onStopTrackingTouch(Slider slider) {
                    timeRange = String.valueOf(slider.getValue());
                    recordLengthButton.setText("Record for (s): "+timeRange+" seconds");
                    Log.e("AZURE", "timeRange slider 2 = " + timeRange);

                }
            };


    //Inner class that performs the recording in the background, as FFmpeg commands block the
    //main thread.
    private class RecordTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(Void... params) {
            // FFmpeg command to record the RTSP stream
            String[] command = {"-ss", "0", "-i", streamPath,
                    "-t", timeRange, directory.getAbsolutePath()+outputFile.toString()};

            Log.e("AZURE","isRecording in RecordTask = "+isRecording);

            // Run the FFmpeg command
            return FFmpeg.execute(command);

        }//end doInBackground

        @Override
        protected void onPostExecute(Integer result) {

            isRecording=false; //Update recording state.
            Log.e("AZURE","isRecording in RecordTask onPostExecute= "+isRecording);
            Post(createRecordingRequest(Integer.parseInt(currentUserId), cameraid)); //Send the newly created recording info.
            recordButton.setText("Start rec");
            super.onPostExecute(result);
        }
    }//end RecordTask

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Recording");

        //Select directory to save the recorded video to.
        if (!directory.exists()) {
            directory.mkdir();
        }
        Log.e("AZURE","directory = "+directory);

        //Generate a unique file name for each recording from the current time.
        //Uses .MKV, as not all cameras can save to .MP4.
        outputFile = "/r_" + System.currentTimeMillis() + ".mkv";

        //Run the FFmpeg command in the background.
        new RecordTask().execute();
    }//end startRecording


    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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

                Toast.makeText(StreamViewingActivityRemote.this,
                        "Recording Saved.",Toast.LENGTH_LONG).show();
                //Upload
                uploadFile("./Internal storage/Download/",outputFile);

            }//end onResponse

            @Override
            public void onFailure(@NonNull Call<RecordingResponse> call, @NonNull Throwable t) {
                //TODO: Appropriate message here.
                Toast.makeText(StreamViewingActivityRemote.this,
                        "Failed to save recording.",Toast.LENGTH_LONG).show();
            }//end onFailure
        });
    }//end Post

    void uploadFile(String filePath, String name){
        Log.e("AZURE","upload beginning for "+name+" @ "+filePath);
        String userid = getIntent().getStringExtra("currentuserid");

        //cant perform network tasks on main thread
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    // Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    // Create the blob client.
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    // Retrieve reference to a previously created container.
                    Log.e("AZURE","userid = "+userid);
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+userid);

                    //create blob if it doesn't exist - hopefully resolves bugs
                    container.createIfNotExists();

                    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
                    CloudBlockBlob blob = container.getBlockBlobReference(name);
                    File source = new File(filePath);
                    blob.uploadFromFile(directory.getAbsolutePath()+"/"+name);
                    Log.d("AZURE","upload function completed");
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Log.e("AZURE","upload failed: "+e);
                }
                return null;
            }
        };
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isRecording==false)
            this.finish();
        else{
            Toast.makeText(StreamViewingActivityRemote.this,
                    "Can't exit while recording.",Toast.LENGTH_LONG).show();
        }

    }

}//end StreamViewingActivityRemote