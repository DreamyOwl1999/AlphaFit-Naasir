package com.example.alphafit.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.google.android.material.navigation.NavigationView;

public class ViewAnalysisActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    private DrawerLayout drawer;
    private SectionsPageAdapter mSectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analysis);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        drawer = findViewById(R.id.drawer_layout_view_analysis);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                startActivity(new Intent(ViewAnalysisActivity.this, HomeActivity.class));
                break;

            case R.id.nav_goals:
                startActivity(new Intent(ViewAnalysisActivity.this, GoalsActivity.class));
                break;

            case R.id.nav_view_analysis:
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_view_add_weight:
                startActivity(new Intent(this, AddWeightActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(ViewAnalysisActivity.this, SettingsActivity.class));
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
