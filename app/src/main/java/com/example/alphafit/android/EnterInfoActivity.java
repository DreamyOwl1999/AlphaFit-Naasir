package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alphafit.R;
import com.example.alphafit.Sys;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EnterInfoActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editFirstName, editLastName, editWeight, editHeight;
    Spinner spinnerGender, spinnerActivity;
    DatePicker spinnerBirth;

    private static final String TAG = "EnterInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        editFirstName = (EditText) findViewById(R.id.firstNameBox);
        editLastName = (EditText) findViewById(R.id.lastNameBox);
        spinnerGender = (Spinner) findViewById(R.id.genderSpinner);
        spinnerActivity = (Spinner) findViewById(R.id.activitySpinner);
        editWeight = (EditText) findViewById(R.id.weightBox);
        editHeight = (EditText) findViewById(R.id.heightBox);
        spinnerBirth = (DatePicker) findViewById(R.id.birthSpinner);
        Button buttonSubmit = (Button) findViewById(R.id.nextButton);

        buttonSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //no need to check view there's only one button
        String fName = editFirstName.getText().toString().trim();
        if(Sys.checkForNumber(fName))
        {
            Toast.makeText(this, "There is a number in your first name", Toast.LENGTH_LONG).show();
            return;
        }
        String lName = editLastName.getText().toString().trim();
        if(Sys.checkForNumber(lName))
        {
            Toast.makeText(this, "There is a number in your last name", Toast.LENGTH_LONG).show();
            return;
        }
        String gender = spinnerGender.getSelectedItem().toString();
        String activity = spinnerActivity.getSelectedItem().toString();
        String weight = editWeight.getText().toString().trim();
        double weightNum = Sys.convertToDouble(weight);
        if(weightNum > 200 || weightNum < 40)
        {
            Toast.makeText(this, "Seems like an impossible weight...", Toast.LENGTH_SHORT).show();
            return;
        }
        String height = editHeight.getText().toString().trim();
        double heightNum = Sys.convertToDouble(height);
        if(heightNum > 2.2 || heightNum < 1)
        {
            Toast.makeText(this, "Seems like an impossible height...", Toast.LENGTH_SHORT).show();
            return;
        }
        int dayOfMonth = spinnerBirth.getDayOfMonth();
        int month = spinnerBirth.getMonth() + 1; //for some reason month needs +1
        int year = spinnerBirth.getYear();
        LocalDate birth = LocalDate.of(year, month, dayOfMonth);
        LocalDate today = LocalDate.now();
        long years = birth.until(today, ChronoUnit.YEARS);
        if(years < 14)
        {
            Toast.makeText(this, "The minimum age for this application is 14.", Toast.LENGTH_LONG).show();
            return;
        }
        Sys system = Sys.getInstance();
        system.setUser(true, fName, lName, gender, weight, height, activity, dayOfMonth, month, year);
        Intent intent = new Intent(this, MainGoalActivity.class);
        intent.putExtra("HEIGHT", height);
        intent.putExtra("WEIGHT", weight);
        startActivity(intent);
    }
}