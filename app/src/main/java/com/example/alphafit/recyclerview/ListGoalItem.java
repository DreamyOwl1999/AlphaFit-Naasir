package com.example.alphafit.recyclerview;

import java.time.LocalDate;

public class ListGoalItem
{
    private String name;
    private String description;
    private boolean completed;
    private String dateShow;

    public ListGoalItem(String n, String desc, boolean c, LocalDate start, LocalDate end)
    {
        name = n;
        description = desc;
        completed = c;
        if(start != null) {
            dateShow = start.toString() + " until " + end.toString();
        }
        else
        {
            dateShow = "";
        }
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getCompleted()
    {
        return completed;
    }

    public String getDateShow()
    {
        return dateShow;
    }
}