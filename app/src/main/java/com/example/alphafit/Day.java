package com.example.alphafit;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Day
{
    private int recIntake, totalInput, totalOutput;
    private LocalDate date;
    private Meal breakfast, lunch, dinner, snacks;
    private ArrayList<Workout> workouts;

    private static final String TAG = "Day";

    public Day(LocalDate day, int intake) //constructor for creating new day in app
    {
        date = day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateForId = formatter.format(date);
        recIntake = intake;
        breakfast = new Meal("Breakfast", "breakfast", 0, 0, 0);
        lunch = new Meal("Lunch", "lunch", 0, 0, 0);
        dinner = new Meal("Dinner", "dinner", 0, 0, 0);
        snacks = new Meal("Snacks", "snacks", 0, 0, 0);
        workouts = new ArrayList<>();
        breakfast.setDocID("breakfast " + dateForId);
        lunch.setDocID("lunch " + dateForId);
        dinner.setDocID("dinner " + dateForId);
        snacks.setDocID("snacks " + dateForId);
    }

    public void updateIntake(int newIntake)
    {
        recIntake = newIntake;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void calculateInput()
    {
        totalInput = breakfast.getTotalCalories() + lunch.getTotalCalories() + dinner.getTotalCalories() + snacks.getTotalCalories();
    }

    public int getcalculateInput()
    {
       return totalInput = breakfast.getTotalCalories() + lunch.getTotalCalories() + dinner.getTotalCalories() + snacks.getTotalCalories();
    }

    public void calculateOutput()
    {
        totalOutput = 0;
        for(int i=0; i<workouts.size(); i++)
        {
            totalOutput += workouts.get(i).getTotalCalories();
        }
    }

    public int getcalculateOutput()
    {
        totalOutput = 0;
        for(int i=0; i<workouts.size(); i++)
        {
            totalOutput += workouts.get(i).getTotalCalories();
        }

        return totalOutput;
    }

    public int[] getCalorieInfo()
    {
        calculateInput();
        calculateOutput();
        return new int[]{recIntake, totalInput, totalOutput};
    }


    public Meal getMeal(String click)
    {
        if(click.equals("breakfast"))
        {
            return breakfast;
        }
        if(click.equals("lunch"))
        {
            return lunch;
        }
        if(click.equals("dinner"))
        {
            return dinner;
        }
        else //snacks
        {
            return snacks;
        }
    }

    public void addMeal(Meal m, String type)
    {
        if(type.equals("breakfast"))
        {
            breakfast = m;
        }
        if(type.equals("lunch"))
        {
            lunch = m;
        }
        if(type.equals("dinner"))
        {
            dinner = m;
        }
        if(type.equals("snacks"))
        {
            snacks = m;
        }
    }
    public ArrayList<Workout> getWorkouts()
    {
        return workouts;
    }

    public void addWorkout(Workout workout)
    {
        workouts.add(workout);
    }

    public void replaceWorkout(int position, Workout workout)
    {
        if(workouts.size() < position)
        {
            Log.d(TAG, "replaceWorkout: out of bounds removal");
            return;
        }
        workouts.set(position, workout);
    }
}