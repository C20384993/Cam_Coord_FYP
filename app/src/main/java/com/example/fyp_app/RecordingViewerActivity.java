package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;

//Play the downloaded recording using ExoPlayer.
public class RecordingViewerActivity extends AppCompatActivity {

    //ExoPlayer object,
    private ExoPlayer player;

    //Activity Variables
    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //The file object the recording is saved to.
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String cameraId;
    String rtspUrl;
    String streamPath;
    String recordingName;
    String recordingId;
    String relativeFilepath;
    String creationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_viewer);

        //Get intent values.
        currentUserId = getIntent().getStringExtra("userid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        cameraId = getIntent().getStringExtra("cameraid");
        rtspUrl = getIntent().getStringExtra("rtspurl");
        streamPath = getIntent().getStringExtra("streampath");
        recordingName = getIntent().getStringExtra("recordingname");
        recordingId = getIntent().getStringExtra("recordingid");
        relativeFilepath = getIntent().getStringExtra("relativefilepath");
        creationDate = getIntent().getStringExtra("creationdate");

        //Setup the ExoPlayer object and play the file.
        StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(RecordingViewerActivity.this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(directory.getAbsolutePath()+"/"+ recordingName);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        //Delete file from downloads folder
        File recording = new File(directory.getAbsolutePath() + "/" + recordingName);
        recording.delete();
        super.onStop();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //Delete file from downloads folder
        File recording = new File(directory.getAbsolutePath()+"/"+ recordingName);
        recording.delete();
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        //Delete file from downloads folder
        File recording = new File(directory.getAbsolutePath() + "/" + recordingName);
        recording.delete();
        super.onDestroy();
        this.finish();
    }

}