package com.example.alphafit;

import java.util.ArrayList;
import java.util.Collections;

public class ExerciseList
{
    private ArrayList<Exercise> exerciseList;

    public ExerciseList()
    {
        exerciseList = new ArrayList<Exercise>();
    }

    public ArrayList<Exercise> getExerciseListList() {
        return exerciseList;
    }

    public void AddExercise (Exercise ex)
    {
        this.exerciseList.add(ex);
    }

    public ArrayList<Exercise> GetRecommendation(int calories)
    {
        ArrayList <Exercise>  ExerciseRecom = new ArrayList<Exercise>();



        for (Exercise e: this.exerciseList)
        {
            System.out.println(e);

            if ( e.getCalories() <= calories )
            {
                ExerciseRecom.add(e);
            }


            Collections.sort(ExerciseRecom, Collections.reverseOrder(new SortByValueExercise()));
        }

        this.PrintList(ExerciseRecom);
        return ExerciseRecom;


    }

    public static void PrintList ( ArrayList <Exercise> recom)
    {
        String theList = "{ ";
        for ( Exercise meal2: recom)
        {
            theList = theList + meal2.toString() + " -> \n";


        }

        theList = theList + " }";
        System.out.println(theList);
    }


    public String toString()
    {
        String theList = "{ ";
        for ( Exercise ex: exerciseList){
            theList = theList + ex.toString() + " -> \n";

        }

        theList =  theList + " }";
        return theList;
    }
}