package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.android.HomeActivity;

import java.text.DecimalFormat;

public class MainGoalActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner goalSpinner;
    private EditText targetWeightBox;
    private EditText weeksBox;
    private Intent prevIntent;
    private double minWeight;
    private double maxWeight;

    private static final String TAG = "MainGoalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: setting up");
        setContentView(R.layout.activity_main_goal);
        goalSpinner = (Spinner) findViewById(R.id.goalSpinner);
        targetWeightBox = (EditText) findViewById(R.id.targetWeightBox);
        weeksBox = (EditText) findViewById(R.id.weeksBox);
        Button saveButton = (Button) findViewById(R.id.saveGoalButton);
        saveButton.setOnClickListener(this);
        prevIntent = getIntent();
        double weight = Double.parseDouble(prevIntent.getStringExtra("WEIGHT"));
        double height = Double.parseDouble(prevIntent.getStringExtra("HEIGHT"));
        double BMI = weight/(height*height);
        minWeight = 18.5 * height * height;
        maxWeight = 30 * height * height;
        TextView txtBMI = findViewById(R.id.bmiBox);
        TextView bmiMessage = findViewById(R.id.BMI_message);
        String message;
        if(BMI < 18.5) //underweight//
        {
            message = "Your BMI is in the underweight range. We recommend that you speak to your GP and consider trying to gain weight.";
            txtBMI.setTextColor(Color.YELLOW);
        }
        else if(BMI>=18.5 && BMI<25) //healthy weight
        {
            message = "Your BMI is in the healthy weight range. We recommend that you keep your BMI between 18.5 and 25";
            txtBMI.setTextColor(Color.GREEN);
        }
        else if(BMI>=25 && BMI<30) //overweight
        {
            message = "Your BMI is in the overweight range. We recommend that you try to lose some weight to get to a BMI of under 25";
            txtBMI.setTextColor(Color.YELLOW);
        }
        else //obese
        {
            message = "Your BMI is in the obese range. We recommend you consult your GP and try to lose weight";
            txtBMI.setTextColor(Color.RED);
        }
        DecimalFormat val = new DecimalFormat("#.#");
        String BMIstr = val.format(BMI);
        txtBMI.setText("Your BMI is " + BMIstr);
        txtBMI.setVisibility(View.VISIBLE);
        bmiMessage.setText(message);
        bmiMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v)
    {
        //don't need to check the view since there is only one button on screen
        //saving the goal happens here
        double userWeight = Double.parseDouble(prevIntent.getStringExtra("WEIGHT"));
        if(userWeight == -1)
        {
            //there has been an error
            //note this currently doesn't solve the problem, just gets user stuck in a loop of repeated clicking
            Log.d(TAG, "onClick: Error in getting intent extra");
            return;
        }
        int weeks = Integer.parseInt(weeksBox.getText().toString());
        String choice = goalSpinner.getSelectedItem().toString();
        double goalWeight = Double.parseDouble(targetWeightBox.getText().toString());
        double weightDiff = userWeight-goalWeight;
        double weeklyChange = weightDiff/weeks;
        Sys system = Sys.getInstance();
        double BMR = system.getUserBMR();
        //checking the user's target weight matches the goal
        if(choice.equals("Lose Weight"))
        {
            if(goalWeight >= userWeight)
            {
                Toast.makeText(this,"Please enter a weight lower than your current weight", Toast.LENGTH_SHORT).show();
                return;
            }
            if(goalWeight < minWeight)
            {
                Toast.makeText(this, "You have entered an unhealthily low weight. Please enter a higher goal", Toast.LENGTH_LONG).show();
                return;
            }
            //check if more than 1kg change per week
            if(weeklyChange > 1)
            {
                Toast.makeText(this, "This goal requires weight loss of more than 1kg/week. Please enter a more reasonable time frame", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "onClick: BMR is" + Double.toString(BMR));
            double recIntake = BMR - (weeklyChange * 1000);
            Log.d(TAG, "onClick: Recommended intake is " + Double.toString(recIntake));
            if(recIntake < 1200)
            {
                Toast.makeText(this, "The calorie deficit for this goal is too low. Please enter a more reasonable time frame", Toast.LENGTH_LONG).show();
                return;
            }

        }
        else if(choice.equals("Maintain Weight"))
        {
            //REMINDER need to have it so the edit texts disappear for this case or are just discounted completely
        }
        else if(choice.equals("Gain Weight"))
        {
            if(goalWeight <= userWeight)
            {
                Toast.makeText(this, "Please enter a weight higher than your current weight", Toast.LENGTH_SHORT).show();
                return;
            }
            if(goalWeight > maxWeight)
            {
                Toast.makeText(this, "You have entered an unhealthily high weight. Please enter a lower goal", Toast.LENGTH_LONG).show();
                return;
            }
            //check if more than 1kg change per week
            weeklyChange *= -1;
            if(weeklyChange > 1)
            {
                Toast.makeText(this, "This goal requires weight gain of more than 1kg/week. Please enter a more reasonable time frame", Toast.LENGTH_LONG).show();
                return;
            }
        }
        system.setUserGoal(weeks, goalWeight, choice);
        //launches the daily activity as registration is finished
        Log.d(TAG, "onClick: Finished! going to daily activity");
        Intent intent2 = new Intent(this, HomeActivity.class);
        startActivity(intent2);
    }
}