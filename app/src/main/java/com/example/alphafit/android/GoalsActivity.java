package com.example.alphafit.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.Sys;
import com.example.alphafit.android.goalfragments.NewGoalActivity;
import com.example.alphafit.goals.Goal;
import com.example.alphafit.recyclerview.ListGoalItem;
import com.example.alphafit.recyclerview.MyGoalAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class GoalsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{

    private DrawerLayout drawer;
    private SectionsPageAdapter mSectionsPageAdapter;
    private ArrayList<Goal> goalList;
    private ArrayList<ListGoalItem> itemList;

    private RecyclerView recyclerView;
    private MyGoalAdapter mAdapter;
    private LinearLayoutManager layoutManager;

    private Button addGoalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        drawer = findViewById(R.id.drawer_layout_goals);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //get the text view
        View header = navigationView.getHeaderView(0);
        TextView welcome = (TextView) header.findViewById(R.id.welcome_user);
        Sys sys = Sys.getInstance();
        welcome.setText("Welcome " + sys.getName());

        //goal setup
        setupRecyclerView();

        //set up button
        addGoalButton = findViewById(R.id.new_goal_button);
        addGoalButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    private void setupRecyclerView()
    {
        Sys sys = Sys.getInstance();
        sys.checkGoalCompletion();
        goalList = sys.getGoals();
        itemList = new ArrayList<>();
        for(int i=0; i<goalList.size(); i++)
        {
            //convert each goal into a list item
            itemList.add(Sys.convertToGoalItemFromGoal(goalList.get(i)));
        }
        recyclerView = findViewById(R.id.goals_recycler_view);
        mAdapter = new MyGoalAdapter(itemList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void onClick(View view)
    {
        if(view == addGoalButton)
        {
            Intent intent = new Intent(this, NewGoalActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;

            case R.id.nav_goals:
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_view_analysis:
                startActivity(new Intent(this, ViewAnalysisActivity.class));
                break;

            case R.id.nav_view_add_weight:
                startActivity(new Intent(this, AddWeightActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.nav_logout:
                DBConnector connector = DBConnector.getInstance();
                connector.logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}