package com.example.alphafit.recyclerview;

public class ListItem {
    private String textName;
    private String textInfo;
    private int calories;
    private double weight;

    public ListItem(String n, int c, double w)
    {
        textName = n;
        textInfo = c + " calories per " + Double.toString(w) + " grams";
        calories = c;
        weight = w;
    }

    public String getTextName()
    {
        return textName;
    }

    public String getTextInfo()
    {
        return textInfo;
    }

    public int getCalories()
    {
        return calories;
    }

    public double getWeight()
    {
        return weight;
    }

}
