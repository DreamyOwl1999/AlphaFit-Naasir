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

public class AddIngredientActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener
{
    private static final String TAG = "AddIngredientActivity";

    private Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);
        setupButtons();
    }

    private void setupButtons()
    {
        saveButton = findViewById(R.id.ing_save);
        cancelButton = findViewById(R.id.ing_cancel);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if(view == saveButton)
        {
            //save the ingredient in the database
            EditText nameBox = findViewById(R.id.ing_name);
            EditText weightBox = findViewById(R.id.ing_weight);
            EditText calorieBox = findViewById(R.id.ing_calories);
            String name = nameBox.getText().toString();
            double weight = Sys.convertToDouble(weightBox.getText().toString());
            int calories = Sys.convertToInt(calorieBox.getText().toString());
            DBConnector connector = DBConnector.getInstance();
            connector.uploadIngredient(name, weight, calories, this);
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
            Log.d(TAG, "onComplete: ingredient upload failed");
        }
    }
}