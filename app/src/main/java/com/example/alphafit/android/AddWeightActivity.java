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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.Sys;
import com.example.alphafit.WeightEntry;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;


public class AddWeightActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private DrawerLayout drawer;
    private SectionsPageAdapter mSectionsPageAdapter;
    private Button saveButton;
    private DatePicker picker;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        drawer = findViewById(R.id.drawer_layout_weight);
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

        saveButton = findViewById(R.id.weight_save_button);
        saveButton.setOnClickListener(this);
        picker = findViewById(R.id.weight_picker);
        edit = findViewById(R.id.new_weight_box);
    }

    public void onClick(View v)
    {
        if(v == saveButton)
        {
            LocalDate date = LocalDate.of(picker.getYear(), picker.getMonth()+1, picker.getDayOfMonth());
            double weight = Sys.convertToDouble(edit.getText().toString());
            WeightEntry w = new WeightEntry(date, weight);
            Sys.getInstance().addWeightEntry(w);
            finish();
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
                startActivity(new Intent(this, GoalsActivity.class));
                break;

            case R.id.nav_view_analysis:
                startActivity(new Intent(this, ViewAnalysisActivity.class));
                break;

            case R.id.nav_view_add_weight:
                drawer.closeDrawer(GravityCompat.START);
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
}
