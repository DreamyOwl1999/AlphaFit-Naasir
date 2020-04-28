package com.example.alphafit;

import java.util.Comparator;

public class SortByValue implements Comparator <Meal>{
    public int compare(Meal a, Meal b)
    {
        return (int) (a.getValue() - b.getValue());

    }
}

