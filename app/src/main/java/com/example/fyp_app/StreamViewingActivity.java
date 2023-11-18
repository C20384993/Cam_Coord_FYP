package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: Find out what is stopping RF Camera from making viewable recordings.
//TODO: Decide on location to save recordings.
//TODO: Save file information to SQL db, e.g. FileID. May need a File class?
public class StreamViewingActivity extends AppCompatActivity {

    OkHttpClient client;
    String getURL;
    private static final String postURL = "https://localhost:8443/Files";

    RecordingAPIService recordingAPIService;
    ExoPlayer player;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String outputFile;
    Button recordButton;

    Connection connection;
    File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_viewing);

        client = new OkHttpClient();
        PlayerView playerView = findViewById(R.id.playerView);
        // Create a player instance.
        player = new ExoPlayer.Builder(StreamViewingActivity.this).build();
        playerView.setPlayer(player);
        // Set the media item to be played.
        //player.setMediaItem(MediaItem.fromUri("rtsp://admin:password@<ip address:554>"));
        player.setMediaItem(MediaItem.fromUri("rtsp://admin:majugarzet@192.168.68.142:554")); //Amcrest
        //player.setMediaItem(MediaItem.fromUri("rtsp://admin:password@192.168.68.144:554")); //RFCam
        // Prepare the player.
        player.prepare();
        player.play();

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    try {
                        stopRecording();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
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
        directory = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Generate a unique file name for each recording
        outputFile = directory.getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".mkv";

        // FFmpeg command to record the RTSP stream
        //Amcrest, make sure to switch actual Player stream above as well.
        String[] command = {"-y", "-i", "rtsp://admin:majugarzet@192.168.68.142:554", "-acodec", "copy", "-vcodec", "copy","-t","00:00:20", outputFile.toString() };
        //RFCam
        //String[] command = {"-y", "-i", "rtsp://admin:password@192.168.68.144:554", "-acodec", "copy", "-vcodec", "copy","-t","00:00:20", outputFile.toString() };

        // Run FFmpeg command
        int recordingStatus = FFmpeg.execute(command);
        if (recordingStatus == 0) {
            // Command succeeded
        } else {
            // Command failed
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
    }

    public void Post(Recording recordingRequest){
        Call<RecordingResponse> recordingCall = RecordingAPIClient.getRecordingService()
                .sendRecording(recordingRequest);
        recordingCall.enqueue(new Callback<RecordingResponse>() {
            @Override
            public void onResponse(Call<RecordingResponse> call, Response<RecordingResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(StreamViewingActivity.this, "saved to db",Toast.LENGTH_LONG);
                }
                else{
                    Toast.makeText(StreamViewingActivity.this, "failed to save",Toast.LENGTH_LONG);

                }
            }

            @Override
            public void onFailure(Call<RecordingResponse> call, Throwable t) {
                Toast.makeText(StreamViewingActivity.this, "failed"+t.getLocalizedMessage(),Toast.LENGTH_LONG);
            }
        });
    }


}