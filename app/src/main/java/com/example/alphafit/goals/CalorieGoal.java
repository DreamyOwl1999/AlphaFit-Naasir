package com.example.alphafit.goals;

import com.example.alphafit.CalendarIterator;
import com.example.alphafit.Sys;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/***
 * Class holds goals set by user to stay within 50 cal of their recommended intake
 */

public class CalorieGoal extends Goal{

    public CalorieGoal(LocalDate start, LocalDate end)
    {
        name = "Stay within your calorie recommendation";
        startDate = start;
        endDate = end;
    }

    @Override
    public void checkCompletion() {
        //gets the values from calendar iterator
        Sys sys = Sys.getInstance();
        CalendarIterator iterator = sys.getIterator();
        if(endDate.isAfter(LocalDate.now()))
        {
            return;
        }
        int[] inputData = iterator.getCalorieInput(startDate, endDate); //food eaten
        int[] outputData = iterator.getCalorieTotal(startDate, endDate); //exercise and BMR
        int[] values = new int[inputData.length];
        for(int i=0; i<values.length; i++)
        {
            values[i] = outputData[i] - inputData[i];
        }
        boolean complete = checkEachDay(values);
        //then passes them to check each day, sets the completed value to that
        if(complete)
        {
            nowAchieved();
        }
    }

    private boolean checkEachDay(int[] values)
    {
        for(int i=0; i<values.length; i++)
        {
            if(values[i] > 50 || values[i] < -50)
            {
                return false;
            }
        }
        return true;
    }

    public String getProgression()
    {
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
}