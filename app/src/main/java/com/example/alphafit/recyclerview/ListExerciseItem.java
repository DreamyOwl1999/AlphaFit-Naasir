package com.example.alphafit.recyclerview;

public class ListExerciseItem
{
    private String name, info, type;
    private int calories, duration;
    private double repOrDistance;

    public ListExerciseItem(String n, int c, int d, String extra, String t, double dOrR)
    {
        name = n;
        info = c + " calories per " + d + " minutes" +  ", " + extra;
        calories = c;
        duration = d;
        type = t;
        repOrDistance = dOrR;
    }

    public String getName()
    {
        return name;
    }

    public String getInfo()
    {
        return info;
    }

    public int getCalories()
    {
        return calories;
    }

    public int getDuration()
    {
        return duration;
    }

    public String getType()
    {
        return type;
    }

    public double getRepOrDistance()
    {
        return repOrDistance;
    }
}