package com.example.alphafit;

public abstract class Exercise {
    protected String name;
    protected int calories;
    protected int duration;
    protected int value;

    public Exercise(String n, int c, int d)
    {
        name = n;
        calories = c;
        duration = d;
        this.value = (this.calories)/(this.duration) ;
    }

    public String toString() {

        String retString = " ( "  +  this.name + ", "   + this.duration+ "S, "
                + this.calories + "KCal, " + this.value +" ) ";
        return retString ;
    }

    public String getName()
    {
        return name;
    }

    public int getCalories()
    {
        return calories;
    }

    public int getDuration()
    {
        return duration;
    }

    public int getValue() {
        return value;
    }

    public abstract String getExtraInfo();
    public abstract double getExtraInfoVal();
}