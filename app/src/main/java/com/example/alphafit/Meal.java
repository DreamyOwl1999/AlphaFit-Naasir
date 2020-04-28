package com.example.alphafit;
import android.util.Log;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Meal extends Task {
    private String name;
    private String mealType;
    private float totalWeight;
    private int totalCalories;
    private int value;
    private ArrayList<Ingredient> ingredients;
    private static final String TAG = "Meal";

    public Meal(String name, String mealType, float totalWeight, int totalCalories, int value) {
        super();
        this.name = name;
        this.mealType = mealType;
        this.totalWeight = totalWeight;
        this.totalCalories = totalCalories;
        this.value = value;
        dateTime = LocalDateTime.now();
        ingredients = new ArrayList<Ingredient>();

    }

    public Meal(String name, String mealType)

    {
        super();
        this.name = name;
        this.mealType = mealType;
    }

    private void calculateTotalCalories()
    {
        totalCalories = 0;
        for(int i=0; i<ingredients.size(); i++)
        {
            totalCalories += ingredients.get(i).getCalories();
        }
    }

    private void calculateTotalWeight()
    {
        totalWeight = 0;
        for(int i=0; i<ingredients.size(); i++)
        {
            totalWeight += ingredients.get(i).getWeight();
        }
    }

    public int getTotalCalories() {
        calculateTotalCalories();
        return totalCalories;
    }

    public float getTotalWeight() {
        calculateTotalWeight();
        return totalWeight;
    }

    public float getValue ()
    {
        return value;
    }

    public String getMealType() {
        return mealType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString() {

        String retString = " ( " + this.name + ", "  + this.mealType + ", " + this.totalWeight + "g, "
                + this.totalCalories + "KCal, " + this.value +" ) ";
        return retString ;
    }

    public void addIngredient(Ingredient ingredient)
    {
        ingredients.add(ingredient);
    }

    public void removeIngredient(int position)
    {
        if(ingredients.size() <= position)
        {
            Log.d(TAG, "removeIngredient: size" + ingredients.size());
            Log.d(TAG, "removeIngredient: position " + position);
            Log.d(TAG, "removeIngredient: not the right position to do this");
            return;
        }
        ingredients.remove(position);
    }

    public void replaceIngredient(int position, Ingredient newIngredient)
    {
        if(ingredients.size() <= position)
        {
            Log.d(TAG, "removeIngredient: size" + ingredients.size());
            Log.d(TAG, "removeIngredient: position " + position);
            Log.d(TAG, "removeIngredient: not the right position to do this");
            return;
        }
        ingredients.remove(position);
        ingredients.add(newIngredient);
    }

    public ArrayList<Ingredient> getIngredients()
    {
        return ingredients;
    }
}
