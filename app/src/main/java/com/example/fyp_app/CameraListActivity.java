package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import models.CameraRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraListActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_camera_list);

        String userid = getIntent().getStringExtra("currentuserid");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        recyclerView = findViewById(R.id.recyclerViewCamList);
        progressBar = findViewById(R.id.progressBarCamList);
        addCameraBtn = findViewById(R.id.addCameraBtn);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        camerasAdapter = new CamerasAdapter(cameraList, new CamerasAdapter.ItemClickListener() {
            @Override
            public void onItemClick(CameraRecyclerItem cameraRecyclerItem) {
                Intent intentEditCamera =
                        new Intent(CameraListActivity.this, EditCameraActivity.class);

                intentEditCamera.putExtra("userid",userid);
                intentEditCamera.putExtra("username",username);
                intentEditCamera.putExtra("password",password);
                intentEditCamera.putExtra("cameraid",Integer.toString(cameraRecyclerItem.getCameraid()));
                intentEditCamera.putExtra("customname",cameraRecyclerItem.getCustomname());
                intentEditCamera.putExtra("camusername",cameraRecyclerItem.getCamusername());
                intentEditCamera.putExtra("campassword",cameraRecyclerItem.getCampassword());
                intentEditCamera.putExtra("rtspurl",cameraRecyclerItem.getRtspurl());
                intentEditCamera.putExtra("streampath",cameraRecyclerItem.getStreampath());

                finish();
                startActivity(intentEditCamera);
            }
        });
        recyclerView.setAdapter(camerasAdapter);

        //Display all cameras managed by the user.
        fetchCameras(userid);
        addCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddCamera =
                        new Intent(CameraListActivity.this, AddCameraActivity.class);

                intentAddCamera.putExtra("userid",userid);
                finish();
                startActivity(intentAddCamera);
            }
        });
    }//end onCreate

    //Fills the RecyclerView with all the cameras associated with the current User ID.
    private void fetchCameras(String userid){
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
                        Toast.makeText(CameraListActivity.this, "Error:"+t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
            });
    }//end fetchCameras

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end class