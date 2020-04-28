package com.example.alphafit.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/***
 * Activity used to add new cardio exercises to the user's personal exercise collection
 */

public class AddCardioActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener
{
    Button saveButton, cancelButton;

    private static final String TAG = "AddCardioActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cardio);
        setupButtons();
    }

    private void setupButtons()
    {
        saveButton = findViewById(R.id.exercise_save);
        saveButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.exercise_cancel);
        cancelButton.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if(view == saveButton)
        {
            EditText nameBox = findViewById(R.id.exercise_name);
            EditText durationBox = findViewById(R.id.exercise_duration);
            EditText distanceBox = findViewById(R.id.exercise_distance);
            EditText caloriesBox = findViewById(R.id.exercise_calories);
            String name = nameBox.getText().toString();
            int duration = Sys.convertToInt(durationBox.getText().toString());
            int distance = Sys.convertToInt(distanceBox.getText().toString());
            int calories = Sys.convertToInt(caloriesBox.getText().toString());
            DBConnector.getInstance().uploadCardioExercise(name, duration, calories, distance, this);
        }
        if(view == cancelButton)
        {
            finish();
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.isSuccessful())
        {
            finish();
        }
        else
        {
            Log.d(TAG, "onComplete: error exercise not uploaded");
        }
    }
}
