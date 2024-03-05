package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.slider.Slider;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.util.ArrayList;

import clients.RecordingAPIClient;
import models.Recording;
import models.RecordingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StreamViewingActivity extends AppCompatActivity {

    //VLC Library objects.
    private LibVLC libVlc;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout videoLayout;
    Button recordButton;
    Button switchButton;

    Button recordLengthButton;
    Slider slider;


    private boolean isRecording = false; //Tracks recording state.
    private String outputFile; //The recording filename.

    String timeRange = "10"; //How long the recording will be for. Starter values: 10 seconds, 20s , 30s.

    String currentUserId;
    String cameraid;
    Uri rtspUrl;
    String streamPath;
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamvlc);
        recordButton=findViewById(R.id.btn_record);
        switchButton=findViewById(R.id.btn_switch);

        recordLengthButton=findViewById(R.id.btn_record_lengthVlc);
        recordLengthButton.setClickable(false);

        slider=findViewById(R.id.time_sliderVlc);
        slider.addOnSliderTouchListener(touchListener);

        //Get intents.
        currentUserId = getIntent().getStringExtra("currentuserid");
        cameraid = getIntent().getStringExtra("cameraid");
        rtspUrl = Uri.parse(getIntent().getStringExtra("rtspurl"));
        streamPath = getIntent().getStringExtra("streampath");

        Log.e("AZURE","rtspUrl = "+rtspUrl);

        //Set the options for the VLC player.
        ArrayList<String> options = new ArrayList<>();
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        options.add("--rtsp-tcp");
        options.add("-vvv");

        //Configuring the player.
        libVlc = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVlc);
        videoLayout = findViewById(R.id.videoLayout);

        mediaPlayer.attachViews(videoLayout, null, false, false);
        Media media = new Media(libVlc, rtspUrl);
        media.setHWDecoderEnabled(true, false);

        //Additional options to improve latency and reduce delay.
        media.addOption(":network-caching=150");
        media.addOption(":clock-jitter=0");
        media.addOption(":clock-synchro=0");

        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play(); //Begin playing the stream.

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording==false) {
                    startRecording();
                }//end if
                else if(isRecording==true){
                    Toast.makeText(StreamViewingActivity.this,
                            "Already recording.",Toast.LENGTH_LONG).show();
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Play locally
                if(isRecording == false) {
                    Intent intentStreamViewingRemote =
                            new Intent(StreamViewingActivity.this, StreamViewingActivityRemote.class);
                    intentStreamViewingRemote.putExtra("currentuserid",currentUserId);
                    intentStreamViewingRemote.putExtra("cameraid",cameraid);
                    intentStreamViewingRemote.putExtra("rtspurl",rtspUrl.toString());
                    intentStreamViewingRemote.putExtra("streampath",streamPath);
                    finish();
                    startActivity(intentStreamViewingRemote);
                }

                else{
                    Toast.makeText(StreamViewingActivity.this,
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
            String[] command = {"-y", "-i", rtspUrl.toString(),
                    "-t", timeRange, "-acodec", "copy", "-vcodec", "copy", "-fflags", "nobuffer",
                    directory.getAbsolutePath()+outputFile.toString()};

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
        new StreamViewingActivity.RecordTask().execute();
    }//end startRecording


    @Override
    protected void onStop()
    {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.detachViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mediaPlayer.release();
        libVlc.release();
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

                Toast.makeText(StreamViewingActivity.this,
                        "Recording Saved.",Toast.LENGTH_LONG).show();
                //Upload
                uploadFile("./Internal storage/Download/",outputFile);

            }//end onResponse

            @Override
            public void onFailure(@NonNull Call<RecordingResponse> call, @NonNull Throwable t) {
                //TODO: Appropriate message here.
                Toast.makeText(StreamViewingActivity.this,
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
                    /*
                    // Create a permissions object.
                    BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

                    // Include public access in the permissions object.
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

                    // Set the permissions on the container.
                    container.uploadPermissions(containerPermissions);
                    */

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

}//end StreamViewingActivity