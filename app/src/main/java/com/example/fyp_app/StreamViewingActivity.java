package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.sql.SQLException;

import clients.RecordingAPIClient;
import models.Recording;
import models.RecordingResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@UnstableApi public class StreamViewingActivity extends AppCompatActivity {

    OkHttpClient client;
    ExoPlayer player;
    private boolean isRecording = false;
    private String outputFile;
    Button recordButton;
    File directory;

    // Create an RTSP media source pointing to an RTSP uri.
    //TODO: Use a GET request to find the camera IP, username, and password. Remove hardcoding.
    MediaSource mediaSource =
            new RtspMediaSource.Factory()
                    .createMediaSource(MediaItem.fromUri("rtsp://admin:majugarzet@192.168.68.142:554"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_viewing);

        client = new OkHttpClient();
        PlayerView playerView = findViewById(R.id.playerView);

        //Inside your onCreate method before player.prepare()
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                .setEnableDecoderFallback(true)
                .setEnableAudioTrackPlaybackParams(true);

        //Prepare the player.
        player = new ExoPlayer.Builder(this, renderersFactory).build();
        playerView.setPlayer(player);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();


        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(view -> {
            if (isRecording) {
                try {
                    stopRecording();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                startRecording();
            }
        });
    }//end onCreate

    private void startRecording() {
        isRecording = true;
        recordButton.setText("Stop Recording");

        // Create a directory to save the recorded video
        directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Generate a unique file name for each recording
        outputFile = directory
                .getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".mkv";

        // FFmpeg command to record the RTSP stream
        //Amcrest, make sure to switch actual Player stream above as well.
        String[] command = {"-y", "-i", "rtsp://admin:majugarzet@192.168.68.142:554",
                "-acodec", "copy", "-vcodec", "copy","-t", "-fflags", "nobuffer",
                outputFile.toString()};

        // Run FFmpeg command
        int recordingStatus = FFmpeg.execute(command);

        if (recordingStatus == 0) {
            Toast.makeText(this, "Recording Successfull", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Recording Failed", Toast.LENGTH_SHORT).show();
        }
    }//end startRecording

    private void stopRecording() throws SQLException {
        isRecording = false;
        recordButton.setText("Record");
        //Upload filename to SQL db through REST API
        Post(createRecordingRequest());
    }//end StopRecording

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    public Recording createRecordingRequest(){
        Recording recordingRequest = new Recording();
        recordingRequest.setFilename(outputFile);
        recordingRequest.setCreationdate("2023-11-13T19:02:33");
        recordingRequest.setUserid(1);
        recordingRequest.setRelativefilepath(directory.getAbsolutePath());
        recordingRequest.setCamerasid(1);

        return recordingRequest;
    }//end createRecordingRequest

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


}