package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    //firebase auth object
    private DBConnector connector;

    //progress dialog
    private ProgressDialog progressDialog;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getting DB connector
        connector = DBConnector.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(connector.checkLoggedIn()){
            //close this activity
            finish();
            Log.d(TAG, "onCreate: FOUND A USER");
            //opening home activity
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        Log.d(TAG, "onCreate: NOT FOUND A USER");

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup  = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    //method for user login
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging in, please Wait...");
        progressDialog.show();

        connector.login(this, email, password);
        progressDialog.hide();
        return;
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == textViewSignup) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}