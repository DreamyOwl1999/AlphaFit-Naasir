package com.example.alphafit.goals;

import com.example.alphafit.CalendarIterator;
import com.example.alphafit.Sys;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/***
 * Extension of the goal class
 * For goals where the user tries to exercise on everyday they specified
 */

public class StreakGoal extends Goal
{
    private LocalDate startDate, endDate;
    private boolean[] daysOfWeek;
    private int totalDays;

    public StreakGoal(LocalDate start, LocalDate end, boolean[] list)
    {
        totalDays = 0;
        for(boolean b: list)
        {
            if(b){totalDays++;}
        }
        name = "Streak goal " + totalDays + " days per week";
        startDate = start;
        endDate = end;
        daysOfWeek = list;
    }


    @Override
    public void checkCompletion() {
        //get a boolean list of days with workouts completed from calendar iterator
        CalendarIterator iterator = Sys.getInstance().getIterator();
        boolean[] exerciseData = iterator.getDaysExercised(startDate, endDate);
        //then check each day against the days of the week
        //if any are wrong then break out of loop and completed is false
        for(int i=0; i<exerciseData.length; i++)
        {
            //get the new date
            LocalDate newDate = startDate.plusDays(i);
            if(!checkDayAgainstList(newDate, exerciseData[i]))
            {
                return;
            }
        }
        //if it made it through the whole list then the goal is achieved
        nowAchieved();
    }

    @Override
    public String getProgression() {
        //get the number of days left
        LocalDate today = LocalDate.now();
        long daysLeft = today.until(endDate, ChronoUnit.DAYS);
        return daysLeft + " days to go!";
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    private boolean checkDayAgainstList(LocalDate date, boolean val)
    {
        //so the date is the one corresponding to the value
        int weekDay = date.getDayOfWeek().getValue();
        boolean checker = daysOfWeek[weekDay-1];
        if(checker && !val) //day of the week was scheduled for exercise but user did not exercise
        {
            return false;
        }
        return true;
    }

    public int getTotalDays()
    {
        return totalDays;
    }
}
