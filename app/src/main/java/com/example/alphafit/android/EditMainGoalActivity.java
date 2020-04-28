package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.DecimalFormat;

public class EditMainGoalActivity extends AppCompatActivity implements View.OnClickListener
{
    private Spinner goalSpinner;
    private EditText targetWeightBox;
    private EditText weeksBox;
    private Button saveButton, cancelButton;

    private double minWeight;
    private double maxWeight;

    private static final String TAG = "EditMainGoalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_main_goal);
        setupButtonsMessages();
        setupEdits();
    }

    private void setupButtonsMessages()
    {
        saveButton = findViewById(R.id.saveGoalButton);
        saveButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        Sys sys = Sys.getInstance();
        double weight = sys.getUserHealthData()[0];
        double height = sys.getUserHealthData()[1];
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

    private void setupEdits()
    {
        Sys sys = Sys.getInstance();
        double[] goalInfo = sys.getUserGoalInfo();
        String type = sys.getUserGoalType();
        targetWeightBox = findViewById(R.id.targetWeightBox);
        weeksBox = findViewById(R.id.weeksBox);
        goalSpinner = findViewById(R.id.goalSpinner);
        targetWeightBox.setText(Double.toString(goalInfo[0]));
        weeksBox.setText(Double.toString(goalInfo[1]));
        if(type.equals("Lose Weight"))
        {
            goalSpinner.setSelection(0);
        }
        if(type.equals("Maintain Weight"))
        {
            goalSpinner.setSelection(1);
        }
        if(type.equals("Gain Weight"))
        {
            goalSpinner.setSelection(2);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == cancelButton)
        {
            finish(); //go back to previous activity
        }
        if(view == saveButton)
        {
            //upload the goal to the database
            int weeks = Integer.parseInt(weeksBox.getText().toString());
            String choice = goalSpinner.getSelectedItem().toString();
            double goalWeight = Double.parseDouble(targetWeightBox.getText().toString());
            Sys system = Sys.getInstance();
            double userWeight = system.getUserHealthData()[0];
            double weightDiff = userWeight-goalWeight;
            double weeklyChange = weightDiff/weeks;
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
            finish();
        }
    }
}