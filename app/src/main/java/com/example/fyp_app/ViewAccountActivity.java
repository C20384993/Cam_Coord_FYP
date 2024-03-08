package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import clients.AccountAPIClient;
import clients.CameraAPIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAccountActivity extends AppCompatActivity {

    TextView accountUsername;
    TextView accountPassword;
    TextView revealPass;
    Button btnDeleteAccount;
    Button btnEditUsername;
    Button btnEditPassword;

    String userid;
    String username;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        btnDeleteAccount = findViewById(R.id.btn_deleteAccount);
        btnEditUsername = findViewById(R.id.btn_editUsername);
        btnEditPassword = findViewById(R.id.btn_editPassword);
        revealPass = findViewById(R.id.textView_revealPass);

        accountUsername = findViewById(R.id.viewAccount_username);
        accountPassword = findViewById(R.id.viewAccount_password);

        userid = getIntent().getStringExtra("currentuserid");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        String hiddenPassword = password.replaceAll(".","*");

        //Display username
        accountUsername.setText("Username: "+username);
        accountPassword.setText("Password: "+hiddenPassword);

        btnEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditUsername = new Intent(ViewAccountActivity.this,
                        EditAccountUsernameActivity.class);

                intentEditUsername.putExtra("currentuserid",userid);
                intentEditUsername.putExtra("username",username);
                intentEditUsername.putExtra("password",password);
                finish();
                startActivity(intentEditUsername);
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditPassword = new Intent(ViewAccountActivity.this,
                        EditAccountPasswordActivity.class);

                intentEditPassword.putExtra("currentuserid",userid);
                intentEditPassword.putExtra("username",username);
                intentEditPassword.putExtra("password",password);
                finish();
                startActivity(intentEditPassword);
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAlertDialogue();
            }
        });

        revealPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountPassword.getText().equals("Password: "+hiddenPassword)){
                    Log.e("AZURE","in if");
                    Log.e("AZURE","gettext = "+accountPassword.getText());
                    accountPassword.setText("Password: "+password);
                    revealPass.setText("Click to hide");
                }
                else{
                    Log.e("AZURE","in else");
                    Log.e("AZURE","gettext = "+accountPassword.getText());
                    accountPassword.setText("Password: "+hiddenPassword);
                    revealPass.setText("Click to show");
                }
            }
        });

    }//end onCreate

    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount(userid);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }//end CreateAlertDialogue

    public void deleteAccount(String userid){

        //Delete function call.
        Call<Void> accountCall = AccountAPIClient.getUserService()
                .deleteAccount(userid);

        accountCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account deleted.",Toast.LENGTH_LONG).show();

                Intent intentMainActivity = new Intent(ViewAccountActivity.this,
                        MainActivity.class);

                intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intentMainActivity);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account not deleted.",Toast.LENGTH_LONG).show();
            }
        });
    }//end deleteCamEntry

    @Override
    public void onBackPressed() {
        Intent intentHomeScreen = new Intent(ViewAccountActivity.this,
                HomeScreen.class);

        intentHomeScreen.putExtra("currentuserid",userid);
        intentHomeScreen.putExtra("username",username);
        intentHomeScreen.putExtra("password",password);
        super.onBackPressed();
        this.finish();
        startActivity(intentHomeScreen);
    }
}