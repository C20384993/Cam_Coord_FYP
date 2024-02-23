package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.file.FileInputStream;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

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

    private boolean isRecording = false; //Tracks recording state.
    private boolean isLocal = true; //Tracks recording state.
    private String outputFile; //The recording filename.
    Button recordButton;
    Button switchButton;
    File directory; //The file object the recording is saved to.
    private String transcodedFilePath;
    String userid = null;
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";

    //TODO: Use a GET request to find the camera IP, username, and password. Remove hardcoding.
    //Must be set to the RTSP stream of your own camera. Hardcoding will be removed later on.
    //Uri rtspUri = Uri.parse("rtsp://admin:majugarzet@192.168.68.142:554");
    //Uri rtspUri = Uri.parse("rtsp://192.168.68.131:8554/stream/mystream");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamvlc);
        String currentuserid = getIntent().getStringExtra("currentuserid");
        userid = currentuserid;
        recordButton=findViewById(R.id.btn_record);
        switchButton=findViewById(R.id.btn_switch);
        Uri rtspUri = Uri.parse(getIntent().getStringExtra("rtspurl"));
        Uri remoteUri = Uri.parse(getIntent().getStringExtra("streampath"));
        int currentUserId = Integer.parseInt(getIntent().getStringExtra("currentuserid"));
        String cameraid = getIntent().getStringExtra("cameraid");

        new RecordTask().execute();

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
        Media media = new Media(libVlc, "rtsps://172.166.189.197:8322/cam1"); //CHANGE BACK TO rtspUri
        Media remoteMedia = new Media(libVlc, remoteUri);
        Media localFile = new Media(libVlc, Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/output.ts");
        media.setHWDecoderEnabled(true, false);

        //Additional options to improve latency and reduce delay.
        media.addOption(":network-caching=150");
        media.addOption(":clock-jitter=0");
        media.addOption(":clock-synchro=0");

        mediaPlayer.setMedia(localFile);
        media.release();
        mediaPlayer.play(); //Begin playing the stream.

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording==false) {
                    startRecording();
                }//end if
                else if(isRecording==true){
                    FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
                    isRecording=false; //Update recording state.
                    Post(createRecordingRequest(currentUserId, cameraid)); //Send the newly created recording info.
                    recordButton.setText("Start rec");
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLocal == false) {
                    isLocal = true;
                    mediaPlayer.stop();
                    switchButton.setText("View remotely");
                    //mediaPlayer.setMedia(media);
                    mediaPlayer.play(); //Begin playing the stream.
                }
                else{
                    isLocal = false;
                    mediaPlayer.stop();
                    switchButton.setText("View locally");
                   // mediaPlayer.setMedia(remoteMedia);
                    mediaPlayer.play(); //Begin playing the stream.
                }
            }
        });

    }//end onCreate

    //Inner class that performs the recording in the background, as FFmpeg commands block the
    //main thread.
    private class RecordTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
          /*  // FFmpeg command to record the RTSP stream
            //TODO: Replace with passed intent url, either remote or local
            String[] command = {"-y", "-i", "rtsp://admin:majugarzet@192.168.68.142:554",
                    "-acodec", "copy", "-vcodec", "copy", "-fflags", "nobuffer",
                    directory.getAbsolutePath()+outputFile.toString()};

            // Run the FFmpeg command
            return FFmpeg.execute(command);*/

            directory = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists()) {
                directory.mkdir();
            }
            Log.e("AZURE","directory = "+directory);



            //Generate a unique file name for each recording from the current time.
            //Uses .MKV, as not all cameras can save to .MP4.
            outputFile = "/output.ts";
            String destination = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+outputFile;

            String[] command = {"-i", "rtsps://192.168.68.131:8322/cam1", destination};
            Log.d("AZURE","destination = "+destination);
            Log.d("AZURE","before execute");
            // Run the FFmpeg command
            return FFmpeg.execute(command);
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Recording");

        //Select directory to save the recorded video to.
        directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
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
        Toast.makeText(StreamViewingActivity.this,
                "uploadfile call",Toast.LENGTH_LONG).show();
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