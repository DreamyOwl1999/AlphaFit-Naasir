package com.example.alphafit;

import java.util.Comparator;

public class SortByValueExercise implements Comparator <Exercise>{
    public int compare(Exercise a, Exercise b)
    {
        return (int) (a.getValue() - b.getValue());

    }
}

