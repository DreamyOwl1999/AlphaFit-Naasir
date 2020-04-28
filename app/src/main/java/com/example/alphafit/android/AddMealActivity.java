package com.example.alphafit.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.alphafit.Ingredient;
import com.example.alphafit.Meal;
import com.example.alphafit.listener.OnAddIngredientListener;
import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.Sys;
import com.google.android.material.tabs.TabLayout;

public class AddMealActivity extends AppCompatActivity implements CurrentIngredientsFragment.Communicator, OnAddIngredientListener {

    private static final String TAG = "AddMealActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private String mealType;
    private Meal meal;
    private CurrentIngredientsFragment currentFrag;
    private SearchIngredientsFragment searchFrag;
    private OwnIngredientsFragment ownFrag;
    private RecommendMealsFragment recommendFrag;
    private boolean scheduled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //get the meal to be edited
        Intent intent = getIntent();
        mealType = intent.getStringExtra("MEAL");
        if(mealType.equals(""))
        {
            //something has gone wrong, go back to home activity
            finish();
        }
        scheduled = intent.getBooleanExtra("SCHEDULED", false);
        if(!scheduled) {
            meal = Sys.getInstance().getMeal(mealType);
        }
        else //a scheduled meal so don't get it from the system
        {
            meal = new Meal(mealType, mealType, 0, 0, 0);
        }
        Log.d("TestingTag", "onCreate: scheduled has value " + scheduled);
        //set up the fragments with their interfaces
        currentFrag.setCommunicator(this);
        currentFrag.setScheduled(scheduled);
        searchFrag.setListener(this);
        ownFrag.setListener(this);
        recommendFrag.setListener(this);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        //add the fragments here
        currentFrag = new CurrentIngredientsFragment();
        searchFrag = new SearchIngredientsFragment();
        ownFrag = new OwnIngredientsFragment();
        recommendFrag = new RecommendMealsFragment();
        adapter.addFragment(currentFrag, "Current");
        adapter.addFragment(searchFrag, "Search");
        adapter.addFragment(ownFrag, "Own");
        adapter.addFragment(recommendFrag, "Recommendations");

        viewPager.setAdapter(adapter);
    }

    public Meal getMeal()
    {
        return meal;
    }

    public void stopActivity()
    {
        finish();
    }

    public void addedIngredient(Ingredient ingredient)
    {
        currentFrag.passBackIngredient(ingredient);
    }
}