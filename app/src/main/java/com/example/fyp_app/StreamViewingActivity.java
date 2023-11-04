package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

//TODO: Find out what is stopping RF Camera from making viewable recordings.
//TODO: Decide on location to save recordings.
//TODO: Save file information to SQL db, e.g. FileID. May need a File class?
public class StreamViewingActivity extends AppCompatActivity {

    ExoPlayer player;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String outputFile;
    Button recordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_viewing);

        PlayerView playerView = findViewById(R.id.playerView);
        // Create a player instance.
        player = new ExoPlayer.Builder(StreamViewingActivity.this).build();
        playerView.setPlayer(player);
        // Set the media item to be played.
        //player.setMediaItem(MediaItem.fromUri("rtsp://admin:password@<ip address:554>"));
        //player.setMediaItem(MediaItem.fromUri("rtsp://admin:majugarzet@192.168.68.142:554")); //Amcrest
        player.setMediaItem(MediaItem.fromUri("rtsp://admin:password@192.168.68.144:554")); //RFCam
        // Prepare the player.
        player.prepare();
        player.play();

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
    }//end onCreate

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Stop Recording");

        // Create a directory to save the recorded video
        File directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Generate a unique file name for each recording
        outputFile = directory.getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".mp4";

        // FFmpeg command to record the RTSP stream
        //String[] command = {"-y", "-i", "rtsp://admin:majugarzet@192.168.68.142:554", "-acodec", "copy", "-vcodec", "copy","-t","00:00:20", outputFile.toString() };
        String[] command = {"-y", "-i", "rtsp://admin:password@192.168.68.144:554", "-acodec", "copy", "-vcodec", "copy","-t","00:00:20", outputFile.toString() };

        // Run FFmpeg command
        int recordingStatus = FFmpeg.execute(command);
        if (recordingStatus == 0) {
            // Command succeeded
        } else {
            // Command failed
        }
    }//end startRecording

    private void stopRecording() {
        isRecording = false;
        recordButton.setText("Record");
    }//end StopRecording

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}