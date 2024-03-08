package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HomeScreen extends AppCompatActivity {

    private Button btnSensorManage;
    private Button btnRecordings;
    Toolbar toolbar;

    String userid;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        userid = getIntent().getStringExtra("currentuserid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        btnSensorManage = findViewById(R.id.btnMainActivity_SensorManage);
        btnRecordings = findViewById(R.id.btnMainActivity_Recordings);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSensorManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCameraList = new Intent(HomeScreen.this,
                        CameraListActivity.class);

                intentCameraList.putExtra("currentuserid",userid);
                intentCameraList.putExtra("username",username);
                intentCameraList.putExtra("password",password);
                startActivity(intentCameraList);
            }
        });

        btnRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecOpt = new Intent(HomeScreen.this,
                        RecordingsOptionsActivity.class);

                intentRecOpt.putExtra("currentuserid",userid);
                intentRecOpt.putExtra("username",username);
                intentRecOpt.putExtra("password",password);
                startActivity(intentRecOpt);
            }
        });

    }//end OnCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myAccount){
            Intent intentEditAccount = new Intent(HomeScreen.this,
                    ViewAccountActivity.class);

            intentEditAccount.putExtra("currentuserid",userid);
            intentEditAccount.putExtra("username",username);
            intentEditAccount.putExtra("password",password);
            startActivity(intentEditAccount);
        }
        if(id == R.id.signOut){
            this.finish();
            Intent intentEditAccount = new Intent(HomeScreen.this,
                    MainActivity.class);
            startActivity(intentEditAccount);
        }
        return true;
    }//end onOptionItemSelected

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent intentEditAccount = new Intent(HomeScreen.this,
                MainActivity.class);
        startActivity(intentEditAccount);
    }

}//end Class