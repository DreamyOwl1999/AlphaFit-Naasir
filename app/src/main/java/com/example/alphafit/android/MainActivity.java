package com.example.alphafit.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.Tetris_UI;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final  String CHANNEL_ID = "personal_notification";
    private final int NOTIFICATION_ID = 001;
    private Button buttonRegister;
    private Button skip;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    private DBConnector connector;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private final int MIN_PASSWORD = 6;
    private static final String TAG = "MainActivity";

    ArrayList<String>  ar= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        connector = DBConnector.getInstance();
        progressDialog = new ProgressDialog(this);

        //if getCurrentUser does not returns null
        connector.refreshUser(this);


        Button buttonRegister = (Button) findViewById(R.id.ButtonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        Log.d(TAG, "onCreate: Started");
        this.displayNotifcation();

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (password.length() < MIN_PASSWORD) {
            Toast.makeText(this, "Please enter password of at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // if validations are ok
        // we will first show a progress bar

        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        connector.registerUser(this, email, password);

        progressDialog.hide();
        return;
    }

    public void tetrisMode(){
        Intent intent = new Intent(MainActivity.this, Tetris_UI.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        //need to check view like this in order to work
        if (view == findViewById(R.id.ButtonRegister)) {
            registerUser();
        }

        if (view == textViewSignin) {
            //will open sign in login activity here
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }

    public void displayNotifcation ()
    {
        Random r = new Random();
        ar.add("Focus on each bite while eating");
        ar.add("Be kind to yourself, exercise!");
        ar.add("Be positive!");
        ar.add("Focus on the present");
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notification);
        builder.setContentTitle("AlphaFit");
        Collections.shuffle(ar);
        builder.setContentText(ar.get(0));
        builder.setPriority( NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Personal Notification";
            String description = "Include all the personal notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }



}