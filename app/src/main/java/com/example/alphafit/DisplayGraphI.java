package com.example.alphafit;

import java.time.LocalDate;
import java.util.ArrayList;

public class DisplayGraphI implements CalendarIterator {


    ArrayList<Day> days;

    private  ArrayList<Day> getDays (LocalDate start, LocalDate end)
    {
        ArrayList<Day> daysRange = new ArrayList<Day>();
        Sys sys = Sys.getInstance();
        ArrayList<Day> days = sys.getCalendar().getDayList();

        for (Day d : days)
        {
            if (d.getDate().isEqual(start))
            {
                daysRange.add(d);
            }

            if (d.getDate().isAfter(start))
            {
                daysRange.add(d);
            }

            if (d.getDate().isEqual(end))
            {
                daysRange.add(d);
                return daysRange;
            }
        }

        return daysRange;

    }
    public boolean[] getDaysExercised(LocalDate start, LocalDate end) {
        days = this.getDays(start, end) ;
        int counter = 0;
        boolean[]  daysExercised = new boolean[days.size()];

        for (Day d: days)
        {
            if (d.getcalculateOutput() == 0)
            {
                daysExercised[counter] = false;
            }

            else if (d.getcalculateOutput() > 0)
            {
                daysExercised[counter] = true;
            }
            counter ++;
        }

        return daysExercised;
    }

    public int[] getCalorieOutput(LocalDate start, LocalDate end)
    {
        days = this.getDays(start, end);
        int counter = 0;
        int [] calorieOutput = new int [days.size()];

        for (Day d: days)
        {
            calorieOutput[counter] = d.getcalculateOutput();
            counter++;
        }


        return  calorieOutput;
    }

    public int[] getCalorieInput(LocalDate start, LocalDate end)
    {
        days = this.getDays(start, end);
        int counter = 0;
        int [] calorieInput = new int [days.size()];

        for (Day d: days)
        {
            calorieInput[counter] = d.getcalculateInput();
            counter++;
        }


        return  calorieInput;
    }

    public int[] getCalorieTotal(LocalDate start, LocalDate end)
    {
        days = this.getDays(start, end);
        int counter = 0;
        int [] calorieTotal = new int [days.size()];

        for (Day d: days)
        {
            calorieTotal[counter] = d.getcalculateInput() + d.getCalorieInfo()[0];
            counter++;
        }


        return  calorieTotal;

    }

    public int[] getTotalDuration(LocalDate start, LocalDate end)
    {
        days = this.getDays(start, end);
        int counter = 0;
        int [] allDurations = new int [days.size()];
        int duration =0;

        for (Day d: days)
        {
            duration  = 0;
            for (Workout w: d.getWorkouts())
            {
                duration += w.getTotalDuration();
            }
            allDurations[counter] = duration;

            counter++;
        }


        return  allDurations;
    }

}
