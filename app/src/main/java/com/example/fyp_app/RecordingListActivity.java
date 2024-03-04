package com.example.fyp_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import models.RecordingRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordingListActivity extends AppCompatActivity {

    //TODO: Allow user to click on a recording in the RecyclerView, then play/download that recording.
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    RecordingsAdapter recordingsAdapter;
    List<RecordingRecyclerItem> recordingList = new ArrayList<>();
    final private String RESTURL = "http://192.168.68.131:8081";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);

        String userid = getIntent().getStringExtra("currentuserid");
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        recyclerView = findViewById(R.id.recyclerViewRecList);
        progressBar = findViewById(R.id.progressBarRecList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recordingsAdapter = new RecordingsAdapter(recordingList, new RecordingsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(RecordingRecyclerItem recordingRecyclerItem) {
                Intent intentEditCamera =
                        new Intent(RecordingListActivity.this, EditRecordingActivity.class);

                intentEditCamera.putExtra("userid",userid);
                intentEditCamera.putExtra("username",username);
                intentEditCamera.putExtra("password",password);
                intentEditCamera.putExtra("cameraid",Integer.toString(recordingRecyclerItem.getCameraid()));
                intentEditCamera.putExtra("customname",recordingRecyclerItem.getCustomname());
                intentEditCamera.putExtra("relativefilepath",recordingRecyclerItem.getRelativefilepath());
                intentEditCamera.putExtra("creationdate",recordingRecyclerItem.getCreationDate());
                intentEditCamera.putExtra("recordingid",Integer.toString(recordingRecyclerItem.getRecordingid()));
                finish();
                startActivity(intentEditCamera);
            }
        });
        recyclerView.setAdapter(recordingsAdapter);
        fetchRecordings(userid);
    }//end OnCreate

    private void fetchRecordings(String userid){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofitClient()
                .getRecordings(RESTURL+"/Recordings/findall?userid="+userid)
                .enqueue(new Callback<List<RecordingRecyclerItem>>() {
                    @Override
                    public void onResponse(Call<List<RecordingRecyclerItem>> call,
                                           Response<List<RecordingRecyclerItem>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            recordingList.addAll(response.body());
                            recordingsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }//end if
                    }//end onResponse

                    @Override
                    public void onFailure(Call<List<RecordingRecyclerItem>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RecordingListActivity.this,
                                "Error:"+t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }//end onFailure
                });
    }//end fetchRecordings

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end Class
