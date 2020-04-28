package com.example.alphafit;

import android.util.Log;

import java.lang.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class User
{
    public static final int MIN_AGE = 14;
    private String firstName, lastName, sex, mainGoalType;
    private double weight, height, BMI, BMR, targetWeight, diffPerWeek;
    private int activityLevel, dateOfBirth, monthOfBirth, yearOfBirth;
    private static final String TAG = "User";
    public User(String firstName, String lastName, String sex,
    double weight, double height,
    int activityLevel, int  dateOfBirth, int  monthOfBirth, int yearOfBirth)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
        this.activityLevel = activityLevel;
        this.dateOfBirth = dateOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.yearOfBirth = yearOfBirth; 
        
        this.setBMI();
        this.setBMR();
        
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
    
    public String getFullName ()
    {
        return firstName + " " + lastName;
    }
    
    public String getSex()
    {
        return sex;
    }
    
    public void changeSex(String s)
    {
        sex = s;
    }
    
    public double getWeight() 
    {
        return weight;
    }
    
    public double[] getHealthData()
    {
        double[] Healthdata = {weight, height, BMI, BMR, activityLevel};
        return Healthdata;
    }
    
    public String getDOB()
    {
        return dateOfBirth + "." + monthOfBirth + "." + yearOfBirth;
    }

    public int[] getDOBArray()
    {
        int[] dob = {dateOfBirth, monthOfBirth, yearOfBirth};
        return dob;
    }
    
    public void setBMI()
    {
        BMI = weight/(height*height) ;
    }
    
    public int getAge()
    {
        LocalDate today = LocalDate.now();
        LocalDate birth = LocalDate.of(yearOfBirth, monthOfBirth, dateOfBirth);
        int age = (int) birth.until(today, ChronoUnit.YEARS);
        return age;
    }

    public void setWeight(double w)
    {
        weight = w;
    }

    public String getGoalType()
    {
        return mainGoalType;
    }

    public double getTargetWeight()
    {
        return targetWeight;
    }

    public double getDiffPerWeek()
    {
        return diffPerWeek;
    }
    
    public void setBMR()
    {
        if (sex.equals("male"))
        {
            BMR = (88.362 + (13.397*weight) +
            (4.799*height*100) - (5.677* this.getAge()));
        }
        
        else if (sex.equals("female"))
        {
            BMR = (447.593 + (9.247*weight) +
            (3.098*height*100) - (4.330* this.getAge()));
        }
    }

    public void setMainGoal(int weeks, double target, String type)
    {
        targetWeight = target;
        mainGoalType = type;
        if(mainGoalType.equals("Lose Weight"))
        {
            diffPerWeek = (double) (weight-target)/weeks;
        }
        else if(mainGoalType.equals("Gain Weight"))
        {
            diffPerWeek = (double) (target-weight)/weeks;
        }
        else {diffPerWeek = 0;}
    }

    public void setMainGoalFromDatabase(String type, double target, double diff)
    {
        targetWeight = target;
        mainGoalType = type;
        diffPerWeek = diff;
    }

    public int calculateRecIntake()
    {
        setBMR();
        int recIntake = (int) BMR;
        if (activityLevel==0)
        {
            recIntake = (int) (BMR * 1.2);
        }
        if (activityLevel==1)
        {
            recIntake = (int) (BMR * 1.375);
        }
        if (activityLevel==2)
        {
            recIntake = (int) (BMR * 1.55);
        }
        if (activityLevel==3)
        {
            recIntake = (int) (BMR * 1.725);
        }
        if(mainGoalType.equals("Lose Weight"))
        {
            recIntake -= (int) diffPerWeek * 100;
        }
        else if(mainGoalType.equals("Gain Weight"))
        {
            recIntake += (int) diffPerWeek * 100;
        }
        return recIntake;
    }
}