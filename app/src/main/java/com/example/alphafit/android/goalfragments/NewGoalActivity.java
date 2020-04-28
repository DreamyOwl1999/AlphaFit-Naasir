package com.example.alphafit.android.goalfragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.android.goalfragments.CalorieGoalFragment;
import com.example.alphafit.android.goalfragments.DurationGoalFragment;
import com.google.android.material.tabs.TabLayout;

public class NewGoalActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    private CalorieGoalFragment calorieFrag;
    private DurationGoalFragment durationFrag;
    private WeightGoalFragment weightFrag;
    private StreakGoalFragment streakFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        calorieFrag = new CalorieGoalFragment();
        durationFrag = new DurationGoalFragment();
        weightFrag = new WeightGoalFragment();
        streakFrag = new StreakGoalFragment();
        adapter.addFragment(calorieFrag, "Calorie");
        adapter.addFragment(new DurationGoalFragment(), "Duration");
        adapter.addFragment(new WeightGoalFragment(), "Weight");
        adapter.addFragment(streakFrag, "Streak");
        viewPager.setAdapter(adapter);
    }
}