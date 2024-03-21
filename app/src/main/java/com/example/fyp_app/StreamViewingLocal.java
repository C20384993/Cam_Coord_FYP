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

//Play the RTSP stream from the camera using its RTSP URL. This only works locally, i.e. when on
// the same network as the camera. Uses VLC player to display the stream.
public class StreamViewingLocal extends AppCompatActivity {

    //VLC Library objects.
    private LibVLC libVlc;
    private VLCVideoLayout videoLayout;
    private MediaPlayer mediaPlayer;

    //Layout items
    Button buttonStartRecording;
    Button buttonSwitchSource;

    //Activity Variables
    private boolean isRecording = false; //Tracks recording state.
    boolean forcedClose = false; //Tracks if app was forcefully closed by user.
    private String outputFile; //The recording filename.
    String currentUserId;
    String cameraId;
    Uri rtspUrl;
    String streamPath;
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_local);

        //Locate items from layout.
        buttonStartRecording =findViewById(R.id.button_StartRecording);
        buttonSwitchSource =findViewById(R.id.button_SwitchSource);

        //Get intents.
        currentUserId = getIntent().getStringExtra("currentuserid");
        cameraId = getIntent().getStringExtra("cameraid");
        rtspUrl = Uri.parse(getIntent().getStringExtra("rtspurl"));
        streamPath = getIntent().getStringExtra("streampath");

        Log.e("AZURE","rtspUrl = "+rtspUrl);

        //Set the options for the VLC player.
        ArrayList<String> options = new ArrayList<>();
        options.add("--no-drop-late-frames");
        options.add("--no-skip-frames");
        options.add("--rtsp-tcp");
        options.add("-vvv");

        //Configuring the player and set the media source.
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
                //Play remotely
                if(isRecording == false) {
                    Intent intentStreamViewingRemote =
                            new Intent(StreamViewingLocal.this,
                                    StreamViewingRemote.class);
                    intentStreamViewingRemote.putExtra("currentuserid",currentUserId);
                    intentStreamViewingRemote.putExtra("cameraid", cameraId);
                    intentStreamViewingRemote.putExtra("rtspurl",rtspUrl.toString());
                    intentStreamViewingRemote.putExtra("streampath",streamPath);
                    finish();
                    startActivity(intentStreamViewingRemote);
                }

                //Don't allow user to switch stream source if a recording is being made.
                else{
                    Toast.makeText(StreamViewingLocal.this,
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
        //Uses .MKV as the media format for compatibility with more cameras.
        outputFile = "r_" + System.currentTimeMillis() + ".mkv";

        //Run the FFmpeg command in the background.
        new StreamViewingLocal.RecordTask().execute();
    }//end startRecording

    @Override
    protected void onPause() {
        if(isRecording==true){
            forcedClose = true;
            FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
        }
        File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
        recording.delete();
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.detachViews();
        this.finish();
    }

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
        mediaPlayer.stop();
        mediaPlayer.detachViews();
        this.finish();
    }

    @Override
    protected void onDestroy()
    {
        if(isRecording==true){
            forcedClose = true;
            FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
        }
        File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
        recording.delete();
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

                Toast.makeText(StreamViewingLocal.this,
                        "Recording Saved.",Toast.LENGTH_LONG).show();
                //Upload the recording.
                uploadFile("./Internal storage/Download/",outputFile);

            }//end onResponse

            @Override
            public void onFailure(@NonNull Call<RecordingResponse> call, @NonNull Throwable t) {
                Toast.makeText(StreamViewingLocal.this,
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
                    Toast.makeText(StreamViewingLocal.this,
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
            File recording = new File(directory.getAbsolutePath()+"/"+outputFile);
            recording.delete();
            super.onBackPressed();
            this.finish();
        }
        else{
            Toast.makeText(StreamViewingLocal.this,
                    "Can't exit while recording.",Toast.LENGTH_LONG).show();
        }
    }

}//end StreamViewingActivity