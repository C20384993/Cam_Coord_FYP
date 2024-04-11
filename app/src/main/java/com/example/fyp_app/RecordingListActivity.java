package com.example.fyp_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import clients.RetrofitClient;
import models.CameraRecyclerItem;
import models.RecordingRecyclerItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Show a list of recordings to the user. They are the recordings the user has made.
public class RecordingListActivity extends AppCompatActivity {

    //Layout items
    TextView textViewNoDataMessage;
    RecyclerView recyclerView;
    SearchView searchView;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    RecordingsAdapter recordingsAdapter;

    //Activity Variables
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    List<RecordingRecyclerItem> recordingList = new ArrayList<>();
    final private String restUrl = "https://c20384993fyp.uksouth.cloudapp.azure.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);

        //Get intent values
        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");

        //Locate items from layout.
        textViewNoDataMessage = findViewById(R.id.textView_NoRecordings);
        recyclerView = findViewById(R.id.recyclerView_RecList);
        searchView = findViewById(R.id.searchView_RecordingList);
        progressBar = findViewById(R.id.progressBarRecList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //When a recording from the list is clicked, show the options page for it.
        recordingsAdapter = new RecordingsAdapter(recordingList, new RecordingsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(RecordingRecyclerItem recordingRecyclerItem) {
                Intent intentRecordingCamera =
                        new Intent(RecordingListActivity.this, EditRecordingActivity.class);

                intentRecordingCamera.putExtra("userid", currentUserId);
                intentRecordingCamera.putExtra("username", currentUsername);
                intentRecordingCamera.putExtra("password", currentPassword);
                intentRecordingCamera.putExtra("salt", currentSalt);
                intentRecordingCamera.putExtra("cameraid",Integer.toString(recordingRecyclerItem.getCameraid()));
                intentRecordingCamera.putExtra("customname",recordingRecyclerItem.getCustomname());
                intentRecordingCamera.putExtra("relativefilepath",recordingRecyclerItem.getRelativefilepath());
                intentRecordingCamera.putExtra("creationdate",recordingRecyclerItem.getCreationDate());
                intentRecordingCamera.putExtra("recordingid",Integer.toString(recordingRecyclerItem.getRecordingid()));
                finish();
                startActivity(intentRecordingCamera);
            }
        });

        recyclerView.setAdapter(recordingsAdapter);

        //Retrieve the list of recordings for the user's account.
        fetchRecordings(currentUserId);

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
    }//end OnCreate

    //Fills the RecyclerView with all the recordings associated with the current User ID.
    private void fetchRecordings(String userid){
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getRetrofitClient()
                .getRecordings(restUrl +"/Recordings/findall?userid="+userid)
                .enqueue(new Callback<List<RecordingRecyclerItem>>() {
                    @Override
                    public void onResponse(Call<List<RecordingRecyclerItem>> call,
                                           Response<List<RecordingRecyclerItem>> response) {
                        if(response.isSuccessful() && !response.body().isEmpty()){
                            recordingList.addAll(response.body());
                            recordingsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            textViewNoDataMessage.setVisibility(View.INVISIBLE);
                        }//end if
                        else if(response.body().isEmpty()){
                            recordingList.addAll(response.body());
                            recordingsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            textViewNoDataMessage.setVisibility(View.VISIBLE);
                        }
                    }//end onResponse

                    @Override
                    public void onFailure(Call<List<RecordingRecyclerItem>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RecordingListActivity.this, "Server unavailable",
                                Toast.LENGTH_SHORT).show();
                    }//end onFailure
                });
    }//end fetchRecordings

    private void filterList(String text){
        List<RecordingRecyclerItem> filteredList = new ArrayList<>();
        for(RecordingRecyclerItem recordingRecyclerItem : recordingList){
            if(recordingRecyclerItem.getCustomname().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(recordingRecyclerItem);
            }
        }
        recordingsAdapter.setFilteredList(filteredList);
    }//end filterList

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end Class
