package com.example.alphafit;

public class StrengthExercise extends Exercise
{
    private int reps;

    public StrengthExercise(String n, int c, int d, int r)
    {
        super(n, c, d);
        reps = r;
    }

    public int getReps()
    {
        return reps;
    }

    public String getExtraInfo()
    {
        return reps + " reps";
    }

    public double getExtraInfoVal()
    {
        return reps;
    }
}
