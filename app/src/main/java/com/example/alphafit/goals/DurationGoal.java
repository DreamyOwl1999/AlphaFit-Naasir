package com.example.alphafit.goals;

import com.example.alphafit.CalendarIterator;
import com.example.alphafit.Sys;

import java.time.LocalDate;

public class DurationGoal extends Goal
{
    int goalDuration;

    public DurationGoal(LocalDate start, LocalDate end, int d)
    {
        name = "Duration of " + goalDuration + " minutes";
        startDate = start;
        endDate = end;
        goalDuration = d;
    }

    @Override
    public void checkCompletion() {
        //will use the calendar iterator to get the total number of minutes between the two dates
        CalendarIterator iterator = Sys.getInstance().getIterator();
        if(endDate.isAfter(LocalDate.now()))
        {
            return;
        }
        //use this to get an int list of durations
        int[] durationData = {1, 1, 1, 1}; //just an example for now
        //then change the completion value
        int totalDuration = 0;
        for(int i=0; i<durationData.length; i++)
        {
            totalDuration += durationData[i];
        }
        if(totalDuration >= goalDuration)
        {
            nowAchieved();
        }
    }

    public String getProgression()
    {
        int minutesLeft = 0; //this will use the calendar iterator
        String description = "You have " + minutesLeft + " minutes to go!";
        return description;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }
}
