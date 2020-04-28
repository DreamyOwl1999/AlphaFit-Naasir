package com.example.alphafit;

public class CardioExercise extends Exercise {
    private double distance;

    public CardioExercise(String n, int c, int d, double dist) {
        super(n, c, d);
        distance = dist;
    }

    public double getDistance()
    {
        return distance;
    }

    public String getExtraInfo()
    {
        return distance + " km";
    }

    @Override
    public double getExtraInfoVal() {
        return distance;
    }
}
