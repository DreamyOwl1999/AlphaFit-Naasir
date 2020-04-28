package com.example.alphafit;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;

public class MealList
{
    private ArrayList<Meal> mealList;

    public MealList()
    {
        mealList = new ArrayList<Meal>();
    }

    public ArrayList<Meal> getMealList() {
        return mealList;
    }

    public void AddMeal ( Meal meal)
    {
        mealList.add(meal);
    }

    public ArrayList<Meal> GetRecommendation(float MaxCalories, String MealType )
    {
        System.out.println(" Max calories = " + MaxCalories);
        ArrayList <Meal>  mealRecom1 = new ArrayList<Meal>();


        Meal m;
        for ( int i = 0; i < mealList.size(); i++)
        {
            m = mealList.get(i);
            if ((m.getTotalCalories() < MaxCalories && m.getMealType().equals(MealType)) ||
                    (m.getTotalCalories() < MaxCalories && MealType.equals("All")))
            {
                mealRecom1.add(m);
            }

        }

        Collections.sort(mealRecom1, new SortByValue());
        this.PrintList(mealRecom1);
        this.PrintList(mealRecom1);
        return mealRecom1;


    }

    public static void PrintList ( ArrayList <Meal> recom)
    {
        String theList = "{ ";
        for ( Meal meal2: recom)
        {
            theList = theList + meal2.toString() + " -> \n";


        }

        theList = theList + " }";
        System.out.println(theList);
    }


    public String toString()
    {
        String theList = "{ ";
        for ( Meal meal2: mealList)
        {
            theList = theList + meal2.toString() + " -> \n";


        }

        theList = theList + " }";
        return theList;
    }
}