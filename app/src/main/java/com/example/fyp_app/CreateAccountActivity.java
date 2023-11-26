package com.example.fyp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import clients.UserAPIClient;
import models.Recording;
import models.User;
import models.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    EditText editTextRegUsername;

    EditText editTextRegPassword;

    Button btnRegAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editTextRegUsername = findViewById(R.id.editText_registerUsername);
        editTextRegPassword = findViewById(R.id.editText_registerPassword);
        btnRegAccount = findViewById(R.id.btn_createAccount);

        btnRegAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }//end onCreate

    public void register(){
        String enteredUsername = editTextRegUsername.getText().toString();
        String enteredPassword = editTextRegPassword.getText().toString();

        //Send POST request if  aren't empty.
        if(TextUtils.isEmpty(enteredUsername)){
            editTextRegUsername.setError("Please enter a username.");
            return;
        }//end if

        else if(TextUtils.isEmpty(enteredPassword)){
            editTextRegPassword.setError("Please enter a password.");
            return;
        }//end else if

        //Else, send POST request.
        else{
            post(createUserRequest(enteredUsername, enteredPassword));
            Toast.makeText(CreateAccountActivity.this,
                    "Account Created",Toast.LENGTH_LONG).show();
            startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
        }//end else

    }//end register

    //Create a model containing the values to be put into the JSON object for the POST request.
    public User createUserRequest(String enteredUsername, String enteredPassword){
        User userRequest = new User();
        userRequest.setUsername(enteredUsername);
        userRequest.setPassword(enteredPassword);

        return userRequest;
    }

    //Send POST request containing the model.
    public void post(User userRequest){
        Call<UserResponse> userCall = UserAPIClient.getUserService().sendUser(userRequest);
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CreateAccountActivity.this,
                            "saved to db",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(CreateAccountActivity.this,
                            "failed to save",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(CreateAccountActivity.this,
                        "failed to save",Toast.LENGTH_LONG);
            }
        });
    }//end post
}