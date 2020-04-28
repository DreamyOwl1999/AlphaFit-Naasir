package com.example.alphafit;

import android.util.Log;

import com.example.alphafit.recyclerview.ListHomeItem;

import java.util.ArrayList;

public class Stats extends Almanac {

    private String s;
    private ArrayList<Ingredient> mealInfo;
    private int type;
    private boolean noti;
    private ListHomeItem item;

    public Stats(int i){
        item = getItem(i);

        if(item.getTitle().equals("Breakast")||item.getTitle().equals("Lunch")||item.getTitle().equals("Dinner")||item.getTitle().equals("Workout")){
            noti=true;
        }
        else{
            noti=false;
        }

        if(item.getTitle().equals("Breakfast")){
            type=0;
        }
        else if(item.getTitle().equals("Lunch")){
            type=1;
        }
        else if(item.getTitle().equals("Dinner")){
            type=2;
        }
        else if(item.getTitle().equals("Snacks")){
            type=3;
        }
        else{
            type=4;
        }

    }

    public String getMealType(){
        return item.getTitle();
    }

    public String getDesc(){
        s=item.getDescription();
        s= s.substring(s.indexOf(",")+1);
        String output = s.substring(0, 1).toUpperCase() + s.substring(1);
        return (output + " " + item.getCalories());

    }

    public boolean getNoti(){
        return noti;
    }

    public int getType(){
        return type;
    }
}
