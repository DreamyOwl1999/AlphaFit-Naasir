package com.example.alphafit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class WeightEntry {
    private LocalDate date;
    private double weight;

    public WeightEntry(LocalDate d, double weight)
    {
        date = d;
        this.weight = weight;

    }

    public double getWeight ()
    {
        return weight;
    }

    public LocalDate getDate() {
        return date;
    }
}