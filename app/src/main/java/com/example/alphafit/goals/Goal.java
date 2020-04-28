package com.example.alphafit.goals;

import java.security.acl.LastOwnerException;
import java.time.LocalDate;
import java.util.Date;

public abstract class Goal
{
    protected Date dateCreated;
    protected boolean achieved;
    protected String name;
    protected LocalDate startDate, endDate;

    public Goal()
    {
        dateCreated = new Date();
        achieved = false;
    }

    public abstract void checkCompletion();
    public abstract String getProgression();
    public abstract LocalDate getStartDate();
    public abstract LocalDate getEndDate();

    protected void nowAchieved()
    {
        achieved = true;
    }

    public boolean getAchieved()
    {
        return achieved;
    }

    public String getName()
    {
        return name;
    }
}