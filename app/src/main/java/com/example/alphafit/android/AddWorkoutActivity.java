package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.Sys;
import com.example.alphafit.Workout;
import com.google.android.material.tabs.TabLayout;

public class AddWorkoutActivity extends AppCompatActivity implements CurrentExercisesFragment.Communicator
{
    private static final String TAG = "AddWorkoutActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private CurrentExercisesFragment currentFrag;
    private SearchExercisesFragment searchFrag;
    private OwnExercisesFragment ownFrag;
    private RecommendWorkoutsFragment recommendFrag;
    private int workoutPosition;

    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        int workoutNo = intent.getIntExtra("TYPE", -1);
        workoutPosition = workoutNo;
        boolean scheduled = intent.getBooleanExtra("SCHEDULED", false);
        currentFrag.setScheduled(scheduled);
        if(workoutNo == -1)
        {
            workout = new Workout("");
            workout.setTime(Sys.getInstance().getSelectedDate().atStartOfDay());
        }
        else
        {
            workout = Sys.getInstance().getSelectedWorkout(workoutNo);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        //make the fragments
        currentFrag = new CurrentExercisesFragment();
        currentFrag.setCommunicator(this);
        searchFrag = new SearchExercisesFragment();
        ownFrag = new OwnExercisesFragment();
        recommendFrag = new RecommendWorkoutsFragment();
        //add them to the adapter
        adapter.addFragment(currentFrag, "Current");
        adapter.addFragment(searchFrag, "Search");
        adapter.addFragment(ownFrag, "Own");
        adapter.addFragment(recommendFrag, "Recommendations");
        searchFrag.setListener(currentFrag);
        ownFrag.setListener(currentFrag);
        recommendFrag.setListener(currentFrag);
        //set the viewpager's adapter
        viewPager.setAdapter(adapter);
    }

    public Workout getWorkout()
    {
        return workout;
    }

    public void stopActivity()
    {
        finish();
    }

    public int getWorkoutPosition()
    {
        return workoutPosition;
    }
}
