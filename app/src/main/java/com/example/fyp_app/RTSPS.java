package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RTSPS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsps);

        // Call the method to run FFmpeg command
        runFFmpegCommand();
    }

    private void runFFmpegCommand() {
        try {
            // Replace this command with your FFmpeg command
            String[] ffmpegCommand = {"ffplay", "-rtsp_transport", "tcp", "-i", "rtsps://localhost:8322/cam1"};
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            output.append("FFmpeg command exited with code ").append(exitCode);

            // Display output in TextView (optional)
            TextView textView = findViewById(R.id.textView);
            textView.setText(output.toString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}