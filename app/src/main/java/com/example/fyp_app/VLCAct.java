package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;


import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;

import java.util.ArrayList;

public class VLCAct extends AppCompatActivity {

    LibVLC libVLC;
    SurfaceView playerView; //Initialized somewhere before
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlcact);
        playerView = findViewById(R.id.surfaceView);


        ArrayList<String> options = new ArrayList<>();
        options.add("--file-caching=2000");
        options.add("-vvv");

        libVLC = new LibVLC(getApplicationContext(), options);
        MediaPlayer player = new MediaPlayer(libVLC);

        IVLCVout vout = player.getVLCVout();
        vout.setVideoView(playerView);
        vout.attachViews();

        Media media = new Media(libVLC, Uri.parse("rtsp://admin:majugarzet@192.168.68.142:554"));

        player.setMedia(media);
        player.play();
    }
}