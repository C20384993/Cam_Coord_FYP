package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class RTSPS extends AppCompatActivity {

    private ExoPlayer player;
    String videoURL="https://172.166.189.197:8888/cam1/index.m3u8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsps);

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

        StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(RTSPS.this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoURL);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
        player.release();
        player = null;
    }

}