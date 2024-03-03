package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import models.CameraRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreamListActivity extends AppCompatActivity {

    FloatingActionButton addCameraBtn;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    CamerasAdapter camerasAdapter;
    List<CameraRecyclerItem> cameraList = new ArrayList<>();
    final private String RESTURL = "http://192.168.68.131:8081";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_list);

        String userid = getIntent().getStringExtra("currentuserid");
        recyclerView = findViewById(R.id.recyclerViewStreamList);
        progressBar = findViewById(R.id.progressBarStreamList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        camerasAdapter = new CamerasAdapter(cameraList, new CamerasAdapter.ItemClickListener() {
            @Override
            public void onItemClick(CameraRecyclerItem cameraRecyclerItem) {
                //Play locally first.
                Intent intentStreamView = new Intent(StreamListActivity.this,
                        StreamViewingActivity.class);

                intentStreamView.putExtra("currentuserid",userid);
                intentStreamView.putExtra("cameraid",Integer.toString(cameraRecyclerItem.getCameraid()));
                intentStreamView.putExtra("rtspurl",cameraRecyclerItem.getRtspurl());
                intentStreamView.putExtra("streampath",cameraRecyclerItem.getStreampath());
                startActivity(intentStreamView);
            }
        });
        recyclerView.setAdapter(camerasAdapter);

        //Display all cameras managed by the user.
        fetchStreams(userid);

    }//end onCreate

    //Fills the RecyclerView with all the cameras associated with the current User ID.
    private void fetchStreams(String userid){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofitClient()
                .getCameras(RESTURL+"/Cameras/findall?userid="+userid)
                .enqueue(new Callback<List<CameraRecyclerItem>>() {
                    @Override
                    public void onResponse(Call<List<CameraRecyclerItem>> call,
                                           Response<List<CameraRecyclerItem>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            cameraList.addAll(response.body());
                            camerasAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }//end if

                    }//end onResponse

                    @Override
                    public void onFailure(Call<List<CameraRecyclerItem>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(StreamListActivity.this, "Error:"+t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }//end fetchCameras
}