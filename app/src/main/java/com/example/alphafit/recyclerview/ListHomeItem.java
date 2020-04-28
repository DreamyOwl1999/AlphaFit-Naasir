package com.example.alphafit.recyclerview;

/***
 * Class used to convert meal or workout into a displayable object in the same list
 */

public class ListHomeItem {
    public static final int MAX_CHARACTERS = 40;
    private String title;
    private int calories;
    private String description;
    private boolean completed;
    private String time;

    public ListHomeItem(String t, int c, String desc, boolean com, String ti)
    {
        title = t;
        calories = c;
        description = desc;
        completed = com;
        time = ti;
    }

    public String getTitle()
    {
        return title;
    }

    public String getTime()
    {
        return time;
    }

    public String getCalories()
    {
        return calories + " cal";
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getCompleted()
    {
        return completed;
    }
}