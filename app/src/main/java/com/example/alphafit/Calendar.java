package com.example.alphafit;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

/***
 * Class which handles the days in it's list and selecting the right day
 */

public class Calendar
{
    public static final LocalDate MAXIMUM_PREVIOUS_DAY = LocalDate.of(2020, 4, 1); //1st of April 2020

    private ArrayList<Day> days;
    private LocalDate selectedDay;
    private LocalDate today;

    public Calendar()
    {
        days = new ArrayList<Day>();
        //get today
        today = LocalDate.now();
        //set selected day to today
        selectedDay = LocalDate.now(); //these need to be separate objects not pointer to same object
        //load up any days from the database (that are necessary, maybe a week prev)
        //check for database days here and load if they exist
        //check for today in days
        //if no days then do below
        createNewDay(LocalDate.now());
    }

    public LocalDate getSelectedDay()
    {
        return selectedDay;
    }

    public String getNiceDate()
    {
        String niceDate;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        niceDate = format.format(selectedDay);
        return niceDate;
    }

    public void moveDayForwardOrBackward(boolean forward)
    {
        LocalDate newDate = LocalDate.of(selectedDay.getYear(), selectedDay.getMonthValue(), selectedDay.getDayOfMonth());
        Log.d(TAG, "moveDayForwardOrBackward: new date is" + newDate.toString());
        if(forward) {
            newDate = newDate.plusDays(1);
        }
        else
        {
            newDate = newDate.plusDays(-1);
        }
        //then check if the new day is in the future
        Log.d(TAG, "moveDayForwardOrBackward: new date is" + newDate.toString());
        if(newDate.isAfter(today))
        {
            //cannot go to the future yet
            return;
        }
        if(newDate.isBefore(MAXIMUM_PREVIOUS_DAY))
        {
            //cannot go beyond here
            return;
        }
        //then check if the day exists in the list
        int position = -1;
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(newDate))
            {
                position = i;
                selectedDay = newDate;
                break;
            }
        }
        if(position == -1) //then the new date does not currently have a Day object
        {
            createNewDay(newDate);
            selectedDay = newDate;
        }
    }

    public boolean moveToSpecificDay(int day, int month, int year)
    {
        //returns true if successful change, false if unsuccessful (out of bounds)
        LocalDate newDate = LocalDate.of(year, month+1, day);
        Log.d(TAG, "moveToSpecificDay: the day is " + newDate.toString());
        //now check if it is out of bounds
        if(newDate.isAfter(today.plusDays(7)))
        {
            Log.d(TAG, "moveToSpecificDay: the day is too far the future");
            return false;
        }
        if(newDate.isBefore(MAXIMUM_PREVIOUS_DAY))
        {
            Log.d(TAG, "moveToSpecificDay: day was too far in the past");
            return false;
        }
        //done all the checks necessary, now change the date
        int position = -1;
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(newDate))
            {
                position = i;
                selectedDay = newDate;
                break;
            }
        }
        if(position == -1) //then the new date does not currently have a Day object
        {
            createNewDay(newDate);
            selectedDay = newDate;
        }
        return true;
    }

    public void createNewDay(LocalDate date) //this may need fixing if the day is in the past: what was the user's weight then? but for now it'll do
    {
        Sys sys = Sys.getInstance();
        Day newDay = new Day(date, sys.getIntake());
        days.add(newDay);
        //then upload this to the database
    }

    public int[] getSelectedInfo()
    {
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(selectedDay))
            {
                return temp.getCalorieInfo();
            }
        }
        return new int[]{-1, -1, -1};
    }

    public Meal getSelectedMeal(String click)
    {
        for(int i=0; i<days.size(); i++) //finds the day that matches the date selected
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(selectedDay))
            {
                return temp.getMeal(click);
            }
        }
        return null;
    }

    public void addNewMeal(Meal m, String type, LocalDate date)
    {
        int pos = -1;
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(date))
            {
                pos = i;
                temp.addMeal(m, type);
            }
        }
        if(pos == -1)
        {
            //day needs to be created
            createNewDay(m.getDate());
            //then search again
            for(int i=0; i<days.size(); i++)
            {
                Day temp = days.get(i);
                if(temp.getDate().isEqual(date))
                {
                    temp.addMeal(m, type);
                }
            }
        }
    }

    public int getNoWorkouts()
    {
        int number = -1;

        for(int i=0; i<days.size(); i++) {
            Day temp = days.get(i);
            if (temp.getDate().isEqual(selectedDay)) {
                number = temp.getWorkouts().size();
                break;
            }
        }
        return number;
    }
    
    public Workout getSelectedWorkout(int position)
    {
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(selectedDay))
            {
                if(position > temp.getWorkouts().size())
                {
                    Log.d(TAG, "getSelectedWorkout: out of bounds");
                    return null;
                }
                return temp.getWorkouts().get(position);
            }
        }
        Log.d(TAG, "getSelectedWorkout: could not find correct day");
        return null;
    }
    
    public void addWorkout(Workout workout, LocalDate date) {
        int pos = -1;
        for (int i = 0; i < days.size(); i++) {
            Day temp = days.get(i);
            if (temp.getDate().isEqual(date)) {
                pos = i;
                temp.addWorkout(workout);
                return;
            }
        }
        if (pos == -1) {
            //day needs to be created
            createNewDay(workout.getDate());
            //then search again
            for(int i=0; i<days.size(); i++)
            {
                Day temp = days.get(i);
                if(temp.getDate().isEqual(date))
                {
                    temp.addWorkout(workout);
                }
            }
        }
    }

    public void replaceWorkout(int position, Workout workout)
    {
        for(int i=0; i<days.size(); i++)
        {
            Day temp = days.get(i);
            if(temp.getDate().isEqual(selectedDay))
            {
                temp.replaceWorkout(position, workout);
                return;
            }
        }
        Log.d(TAG, "replaceWorkout: could not find correct day");
    }

    public ArrayList<Day> getDays(){
        return days;
    }

    public  ArrayList<Day> getDayList()
    {
        return days;
    }



}