package com.example.alphafit;

import androidx.annotation.NonNull;

public class Ingredient
{
    private double weight;
    private int calories;
    private String name;

    public Ingredient(String n, double w, int c)
    {
        name = n;
        weight = w;
        calories = c;
    }

    public double getWeight()
    {
        return weight;
    }

    public int getCalories()
    {
        return calories;
    }

    public String getName()
    {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name + ", " + weight + ", " + calories;
    }
}