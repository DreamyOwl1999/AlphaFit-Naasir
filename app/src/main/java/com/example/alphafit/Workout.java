package com.example.alphafit;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Workout extends Task
{
    private String name;
    private ArrayList<Exercise> exercises;
    private int totalCalories;
    private int totalDuration;

    public Workout(String n)
    {
        super();
        exercises = new ArrayList<>();
        totalCalories = 0;
        totalDuration = 0;
        dateTime = LocalDateTime.now();
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }

    public ArrayList<Exercise> getExercises()
    {
        return exercises;
    }

    private void calculateTotalCalories()
    {
        totalCalories = 0;
        for(int i=0; i<exercises.size(); i++)
        {
            totalCalories += exercises.get(i).getCalories();
        }
    }

    private void calculateTotalDuration()
    {
        totalDuration = 0;
        for(int i=0; i<exercises.size(); i++)
        {
            totalDuration += exercises.get(i).getDuration();
        }
    }

    public int getTotalCalories()
    {
        calculateTotalCalories();
        return totalCalories;
    }

    public int getTotalDuration()
    {
        calculateTotalDuration();
        return totalDuration;
    }

    public void addExercise(Exercise e)
    {
        exercises.add(e);
    }

    public void removeExercise(int position)
    {
        exercises.remove(position);
    }

    public void replaceExercise(int position, Exercise e)
    {
        exercises.set(position, e);
    }
}
