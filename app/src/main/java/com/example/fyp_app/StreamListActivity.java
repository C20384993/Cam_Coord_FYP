package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import models.CameraRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Show a list of streams to the user. They are the cameras the user has added to his account.
public class StreamListActivity extends AppCompatActivity {

    //Layout items
    TextView noDataMessage;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    CamerasAdapter camerasAdapter;
    List<CameraRecyclerItem> cameraList = new ArrayList<>();
    Button buttonDarkMode;

    //Activity variables
    String currentUserId;
    boolean darkMode = false;
    final private String restUrl = "http://172.166.189.197:8081";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_list);

        //Locate items from layout.
        currentUserId = getIntent().getStringExtra("currentuserid");
        recyclerView = findViewById(R.id.recyclerViewStreamList);
        progressBar = findViewById(R.id.progressBarStreamList);

        linearLayoutManager = new LinearLayoutManager(this);

        buttonDarkMode = findViewById(R.id.button_DarkMode);
        recyclerView.setLayoutManager(linearLayoutManager);
        noDataMessage = findViewById(R.id.textView_NoStreamsAlert);

        //When a camera from the list is clicked, start playing the stream locally. This uses
        //the RTSP URL for the camera so that the app gets the stream directly from the camera.
        camerasAdapter = new CamerasAdapter(cameraList, new CamerasAdapter.ItemClickListener() {
            @Override
            public void onItemClick(CameraRecyclerItem cameraRecyclerItem) {
                //Play locally first.
                Intent intentStreamView = new Intent(StreamListActivity.this,
                        StreamViewingLocal.class);

                intentStreamView.putExtra("currentuserid", currentUserId);
                intentStreamView.putExtra("cameraid",Integer
                        .toString(cameraRecyclerItem.getCameraid()));
                intentStreamView.putExtra("rtspurl",cameraRecyclerItem.getRtspurl());
                intentStreamView.putExtra("streampath",cameraRecyclerItem.getStreampath());
                startActivity(intentStreamView);
            }
        });
        recyclerView.setAdapter(camerasAdapter);

        //Display all cameras managed by the user.
        fetchStreams(currentUserId);

        //Dark Mode checks
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            darkMode = false;
            buttonDarkMode.setText("Dark mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.dark));
        }
        else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkMode = true;
            buttonDarkMode.setText("Light mode");
            buttonDarkMode.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        buttonDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(darkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    buttonDarkMode.setText("Dark mode");
                    darkMode = false;
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkMode=true;
                    buttonDarkMode.setText("Light mode");
                }
            }
        });

    }//end onCreate

    //Fills the RecyclerView with all the cameras associated with the current User ID.
    private void fetchStreams(String userid){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofitClient()
                .getCameras(restUrl +"/Cameras/findall?userid="+userid)
                .enqueue(new Callback<List<CameraRecyclerItem>>() {
                    @Override
                    public void onResponse(Call<List<CameraRecyclerItem>> call,
                                           Response<List<CameraRecyclerItem>> response) {
                        if(response.isSuccessful() && !response.body().isEmpty()){
                            cameraList.addAll(response.body());
                            camerasAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }//end if
                        else if(response.body().isEmpty()){
                            cameraList.addAll(response.body());
                            camerasAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            noDataMessage.setVisibility(View.VISIBLE);
                        }

                    }//end onResponse

                    @Override
                    public void onFailure(Call<List<CameraRecyclerItem>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(StreamListActivity.this, "Server unavailable",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }//end fetchCameras
}