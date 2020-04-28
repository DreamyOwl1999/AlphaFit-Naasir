package com.example.alphafit;

import java.time.LocalDate;

public interface CalendarIterator
{
    public boolean[] getDaysExercised(LocalDate start, LocalDate end); //true if exercised on that day, false otherwise

    public int[] getCalorieInput(LocalDate start, LocalDate end); //for food eaten

    public int[] getCalorieTotal(LocalDate start, LocalDate end); //this should include BMR and exercise

    public int[] getCalorieOutput(LocalDate start, LocalDate end); //this is just exercise
}