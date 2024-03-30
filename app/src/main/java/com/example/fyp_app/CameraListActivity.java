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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import clients.RetrofitClient;
import models.CameraRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Display a list of cameras that the user has added to his account.
public class CameraListActivity extends AppCompatActivity {

    //Layout items
    TextView textViewNoCamerasAlert;
    FloatingActionButton buttonAddCamera;
    RecyclerView recyclerViewCamList;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    CamerasAdapter camerasAdapter;
    List<CameraRecyclerItem> cameraList = new ArrayList<>();
    Button buttonRefresh;
    Button buttonDarkMode;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    boolean darkMode = false;
    final private String restUrl = "https://c20384993fyp.uksouth.cloudapp.azure.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);

        //Get intent variables.
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");

        //Locate items from layout.
        recyclerViewCamList = findViewById(R.id.recyclerView_CamList);
        progressBar = findViewById(R.id.progressBar_CamList);
        buttonAddCamera = findViewById(R.id.button_AddCamera);
        buttonRefresh = findViewById(R.id.button_refreshList);
        buttonDarkMode = findViewById(R.id.button_DarkMode);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewCamList.setLayoutManager(linearLayoutManager);
        textViewNoCamerasAlert = findViewById(R.id.textView_NoCamerasAlert);

        //Set a onItemClick listener for the cameraList. When an item is clicked, open an activity.
        camerasAdapter = new CamerasAdapter(cameraList, new CamerasAdapter.ItemClickListener() {
            @Override
            public void onItemClick(CameraRecyclerItem cameraRecyclerItem) {
                Intent intentEditCamera =
                        new Intent(CameraListActivity.this, EditCameraActivity.class);

                //Open the edit camera activity.
                //Pass the current user details and camera item details.
                intentEditCamera.putExtra("userid", currentUserId);
                intentEditCamera.putExtra("username", currentUsername);
                intentEditCamera.putExtra("password", currentPassword);
                intentEditCamera.putExtra("cameraid",Integer.toString(cameraRecyclerItem
                        .getCameraid()));
                intentEditCamera.putExtra("customname",cameraRecyclerItem.getCustomname());
                intentEditCamera.putExtra("camusername",cameraRecyclerItem.getCamusername());
                intentEditCamera.putExtra("campassword",cameraRecyclerItem.getCampassword());
                intentEditCamera.putExtra("rtspurl",cameraRecyclerItem.getRtspurl());
                intentEditCamera.putExtra("streampath",cameraRecyclerItem.getStreampath());

                finish();
                startActivity(intentEditCamera);
            }
        });

        recyclerViewCamList.setAdapter(camerasAdapter);

        //Retrieve data for all cameras managed by the user.
        fetchCameras(currentUserId);

        //Start the add Camera activity to allow user to create a new camera entry.
        buttonAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddCamera =
                        new Intent(CameraListActivity.this, AddCameraActivity.class);

                //Pass current user's details, they are needed to add the camera to his account.
                intentAddCamera.putExtra("userid", currentUserId);
                intentAddCamera.putExtra("username", currentUsername);
                intentAddCamera.putExtra("password", currentPassword);

                finish();
                startActivity(intentAddCamera);
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewCamList.setAdapter(null);
                recyclerViewCamList.setLayoutManager(null);
                recyclerViewCamList.getRecycledViewPool().clear();
                recyclerViewCamList.swapAdapter(camerasAdapter, false);
                recyclerViewCamList.setLayoutManager(linearLayoutManager);
                camerasAdapter.notifyDataSetChanged();
            }
        });

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
                    recyclerViewCamList.setAdapter(null);
                    recyclerViewCamList.setLayoutManager(null);
                    recyclerViewCamList.getRecycledViewPool().clear();
                    recyclerViewCamList.swapAdapter(camerasAdapter, false);
                    recyclerViewCamList.setLayoutManager(linearLayoutManager);
                    camerasAdapter.notifyDataSetChanged();
                    fetchCameras(currentUserId);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkMode=true;
                    buttonDarkMode.setText("Light mode");
                    recyclerViewCamList.setAdapter(null);
                    recyclerViewCamList.setLayoutManager(null);
                    recyclerViewCamList.getRecycledViewPool().clear();
                    recyclerViewCamList.swapAdapter(camerasAdapter, false);
                    recyclerViewCamList.setLayoutManager(linearLayoutManager);
                    camerasAdapter.notifyDataSetChanged();
                    fetchCameras(currentUserId);
                }
            }
        });
    }//end onCreate

    //Retrieve all camera rows for the logged in user.
    //Fill the RecyclerView with all the cameras associated with the current User ID.
    private void fetchCameras(String userid){
        progressBar.setVisibility(View.VISIBLE);
        // Load the certificate file
        InputStream certificateInputStream = getResources().openRawResource(R.raw.server);
        RetrofitClient.getRetrofitClient(certificateInputStream)
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
                            textViewNoCamerasAlert.setVisibility(View.VISIBLE);
                        }
                    }//end onResponse

                    @Override
                    public void onFailure(Call<List<CameraRecyclerItem>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CameraListActivity.this,
                                "Error:"+t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }//end onFailure
            });
    }//end fetchCameras

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end class