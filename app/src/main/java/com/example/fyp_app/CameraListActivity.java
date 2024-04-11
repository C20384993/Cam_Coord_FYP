package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    SearchView searchView;
    Button buttonRefresh;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    final private String restUrl = "https://c20384993fyp.uksouth.cloudapp.azure.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);

        //Get intent variables.
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");

        //Locate items from layout.
        recyclerViewCamList = findViewById(R.id.recyclerView_CamList);
        searchView = findViewById(R.id.searchView_CameraList);
        searchView.clearFocus();
        progressBar = findViewById(R.id.progressBar_CamList);
        buttonAddCamera = findViewById(R.id.button_AddCamera);
        buttonRefresh = findViewById(R.id.button_refreshList);

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
                intentEditCamera.putExtra("salt", currentSalt);
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
                intentAddCamera.putExtra("salt", currentSalt);
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }//end onCreate

    //Retrieve all camera rows for the logged in user.
    //Fill the RecyclerView with all the cameras associated with the current User ID.
    private void fetchCameras(String userid){
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

    private void filterList(String text){
        List<CameraRecyclerItem> filteredList = new ArrayList<>();
        for(CameraRecyclerItem cameraRecyclerItem : cameraList){
            if(cameraRecyclerItem.getCustomname().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(cameraRecyclerItem);
            }
        }
        camerasAdapter.setFilteredList(filteredList);
    }//end filterList

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end class