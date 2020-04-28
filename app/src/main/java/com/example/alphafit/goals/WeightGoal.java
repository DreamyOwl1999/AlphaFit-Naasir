package com.example.alphafit.goals;

import android.util.Log;

import com.example.alphafit.Sys;

import java.time.LocalDate;

public class WeightGoal extends Goal
{
    private double goalWeight;
    private String type;
    private static final String TAG = "WeightGoal";

    public WeightGoal(double target, String type)
    {
        name = type + ": " + target + "kg";
        goalWeight = target;
        this.type = type;
    }

    @Override
    public void checkCompletion() {
        Sys sys = Sys.getInstance();
        double weight = sys.getUserHealthData()[0];
        double diff = getDifference(weight);
        if(diff <= 0)
        {
            nowAchieved();
            Log.d(TAG, "checkCompletion: Goal is achieved!");
        }
        else
        {
            Log.d(TAG, "checkCompletion: Goal is not yet achieved");
        }
    }

    public double getGoalWeight()
    {
        return goalWeight;
    }

    public String getType()
    {
        return type;
    }

    public double getDifference(double userWeight)
    {
        if(type.equals("Lose Weight")) //will either be gain weight or lose weight
        {
            return userWeight - goalWeight;
        }
        else
        {
            return goalWeight - userWeight;
        }
    }

    public String getProgression()
    {
        String description = "You have ";
        description += getDifference(Sys.getInstance().getUserHealthData()[0]) + "kg to go!";
        return description;
    }

    @Override
    public LocalDate getStartDate() {
        return null;
    }

    @Override
    public LocalDate getEndDate() {
        return null;
    }
}