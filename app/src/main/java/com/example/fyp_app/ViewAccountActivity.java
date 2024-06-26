
package com.example.fyp_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import clients.AccountAPIClient;
import clients.RecordingAPIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//The "My Account" page of the app. User can edit his profile from this activity.
public class ViewAccountActivity extends AppCompatActivity {

    //Layout items
    TextView textViewAccountUsername;
    TextView textViewAccountPassword;
    TextView textViewRevealPass;
    Button buttonDeleteAccount;
    Button buttonEditUsername;
    Button buttonEditPassword;
    Button buttonDarkMode;

    //Acitvity variables
    boolean darkMode = false;
    String currentUserId;
    String currentUsername;
    String currentPassword;
    String currentSalt;
    String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=c20384993fypstorage;AccountKey=0/AH0LCag12HGTA1hw+kXlCdj/0fJ9sew5o9nytBW3tac4gFiwpmEgwWOqlA+c4C4hHKg5SdgSCm+ASt4ij9LQ==;EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        //Locate items from layout
        buttonDeleteAccount = findViewById(R.id.button_DeleteAccount);
        buttonEditUsername = findViewById(R.id.button_EditUsername);
        buttonEditPassword = findViewById(R.id.button_EditPassword);
        textViewRevealPass = findViewById(R.id.textView_RevealPass);
        textViewAccountUsername = findViewById(R.id.textView_Username);
        textViewAccountPassword = findViewById(R.id.textView_Password);
        buttonDarkMode = findViewById(R.id.button_DarkMode);

        currentUserId = getIntent().getStringExtra("currentuserid");
        currentUsername = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        currentSalt = getIntent().getStringExtra("salt");

        //Hide the user's password by showing an empty string by default.
        String hiddenPassword = "";

        //Display username
        textViewAccountUsername.setText("Username: "+ currentUsername);
        textViewAccountPassword.setText("Password: "+hiddenPassword);

        //Start the activity to edit the account username.
        buttonEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditUsername = new Intent(ViewAccountActivity.this,
                        EditAccountUsernameActivity.class);

                intentEditUsername.putExtra("currentuserid", currentUserId);
                intentEditUsername.putExtra("username", currentUsername);
                intentEditUsername.putExtra("password", currentPassword);
                intentEditUsername.putExtra("salt", currentSalt);
                finish();
                startActivity(intentEditUsername);
            }
        });

        //Start the activity to edit the account password.
        buttonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditPassword = new Intent(ViewAccountActivity.this,
                        EditAccountPasswordActivity.class);

                intentEditPassword.putExtra("currentuserid", currentUserId);
                intentEditPassword.putExtra("username", currentUsername);
                intentEditPassword.putExtra("password", currentPassword);
                intentEditPassword.putExtra("salt", currentSalt);
                finish();
                startActivity(intentEditPassword);
            }
        });

        //Delete the user's account. Provide a confirmation box before deletion.
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Provide a confirmation box before deletion.
                CreateAlertDialogue();
            }
        });

        //When clicked, show the password for the account in the password text view.
        textViewRevealPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewAccountPassword.getText().equals("Password: "+hiddenPassword)){
                    textViewAccountPassword.setText("Password: "+ currentPassword);
                    textViewRevealPass.setText("Click to hide");
                }
                else{
                    textViewAccountPassword.setText("Password: "+hiddenPassword);
                    textViewRevealPass.setText("Click to show");
                }
            }
        });

        //Dark Mode checks.
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

    //Alert dialogue, user must confirm before his account is deleted.
    private void CreateAlertDialogue() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount(currentUserId);
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

    //Call the REST API to delete the user's account row from the database.
    public void deleteAccount(String userid){

        //Delete function call.
        Call<Void> accountCall = AccountAPIClient.getUserService()
                .deleteAccount(userid);

        accountCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account deleted.",Toast.LENGTH_LONG).show();

                //Account deleted, now return to app landing page.
                Intent intentMainActivity = new Intent(ViewAccountActivity.this,
                        MainActivity.class);

                intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                deleteRecordings();
                finish();
                startActivity(intentMainActivity);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ViewAccountActivity.this,
                        "Account not deleted.",Toast.LENGTH_LONG).show();
            }
        });
    }//end deleteAccount


    //When back button is pressed, finish activity and return to previous one.
    @Override
    public void onBackPressed() {
        Intent intentHomeScreen = new Intent(ViewAccountActivity.this,
                HomeScreen.class);

        intentHomeScreen.putExtra("currentuserid", currentUserId);
        intentHomeScreen.putExtra("username", currentUsername);
        intentHomeScreen.putExtra("password", currentPassword);
        intentHomeScreen.putExtra("salt", currentSalt);
        super.onBackPressed();
        this.finish();
        startActivity(intentHomeScreen);
    }


    //Delete the user's recordings from Blob storage if he deletes his account.
    //Database records will be deleted when account is deleted because of cascading delete.
    public void deleteRecordings(){

        //AsyncTask as it is not possible to perform network tasks on main thread
        AsyncTask<Void,Void,Boolean> task = new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean success = false;
                try
                {
                    //Retrieve storage account from connection-string.
                    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                    //Create the blob client.
                    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                    //Retrieve reference to a previously created container.
                    CloudBlobContainer container = blobClient.getContainerReference("cont"+currentUserId);

                    //Delete the user's recordings folder/container from Blob Storage.
                    container.delete();
                    success = true;
                }
                catch (Exception e)
                {
                    // Output the stack trace.
                    e.printStackTrace();
                    Toast.makeText(ViewAccountActivity.this, "Server Unavailable.", Toast.LENGTH_LONG).show();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
            }
        };

        task.execute();
    }//end deleteRecordings
}
