package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.FFmpeg;

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

    private boolean isRecording = false; //Tracks recording state.
    private String outputFile; //The recording filename.
    Button recordButton;
    File directory; //The file object the recording is saved to.

    //TODO: Use a GET request to find the camera IP, username, and password. Remove hardcoding.
    //Must be set to the RTSP stream of your own camera. Hardcoding will be removed later on.
    Uri rtspUri = Uri.parse("rtsp://admin:majugarzet@192.168.68.142:554");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamvlc);
        recordButton=findViewById(R.id.btn_record);

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
        Media media = new Media(libVlc, rtspUri);
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
                    FFmpeg.cancel(); //Stop the FFmpeg command executing, which stops the recording.
                    isRecording=false; //Update recording state.
                    Post(createRecordingRequest()); //Send the newly created recording info.
                    recordButton.setText("Start rec");
                }
            }
        });

    }//end onCreate

    //Inner class that performs the recording in the background, as FFmpeg commands block the
    //main thread.
    private class RecordTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // FFmpeg command to record the RTSP stream
            String[] command = {"-y", "-i", "rtsp://admin:majugarzet@192.168.68.142:554",
                    "-acodec", "copy", "-vcodec", "copy", "-fflags", "nobuffer",
                    outputFile.toString()};

            // Run the FFmpeg command
            return FFmpeg.execute(command);
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == 0) {
                Toast.makeText(StreamViewingActivity.this, "Recording Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StreamViewingActivity.this, "Recording Successful", Toast.LENGTH_SHORT).show();
            }
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

        //Generate a unique file name for each recording from the current time.
        //Uses .MKV, as not all cameras can save to .MP4.
        outputFile = directory
                .getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".mkv";

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
    public Recording createRecordingRequest(){
        Recording recordingRequest = new Recording();
        recordingRequest.setRecordingname(outputFile);
        recordingRequest.setUserid(1);
        recordingRequest.setRelativefilepath(directory.getAbsolutePath());
        recordingRequest.setCameraid(1);

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

                if(response.isSuccessful()){
                    Toast.makeText(StreamViewingActivity.this,
                            "Saved recording",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(StreamViewingActivity.this,
                            "Failed to save recording",Toast.LENGTH_LONG).show();

                }
            }//end onResponse

            @Override
            public void onFailure(@NonNull Call<RecordingResponse> call, @NonNull Throwable t) {
                Toast.makeText(StreamViewingActivity.this,
                        "failed"+t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }//end onFailure
        });
    }//end Post
}//end StreamViewingActivity