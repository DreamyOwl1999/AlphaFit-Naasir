package com.example.alphafit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/***
 * Parent class of Workout and Meal, shows object as being complete or not for display purposes and holds DocID
 * DocID is used for replacement of meals in the database
 */

public abstract class Task
{
    private boolean completed;
    protected String docID;
    protected LocalDateTime dateTime;

    public Task()
    {
        completed = false;
        docID = "";
    }

    public boolean getCompleted()
    {
        return completed;
    }

    public void updateCompleted(boolean c)
    {
        completed = c;
    }

    public void setDocID(String id)
    {
        docID = id;
    }

    public String getDocID()
    {
        return docID;
    }

    public LocalDate getDate()
    {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime()
    {
        return dateTime.toLocalTime();
    }

    public LocalDateTime getDateTime()
    {
        return dateTime;
    }

    public void setTime(LocalDateTime time)
    {
        dateTime = time;
    }
}