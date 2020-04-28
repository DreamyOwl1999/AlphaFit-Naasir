package com.example.alphafit.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphafit.DBConnector;
import com.example.alphafit.R;
import com.example.alphafit.SectionsPageAdapter;
import com.example.alphafit.Sys;
import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private DrawerLayout drawer;
    private SectionsPageAdapter mSectionsPageAdapter;
    private Button cancelButton, goalButton, saveButton;
    private TextView weight;
    private EditText fName, lName, height;
    private Spinner sex, actLevel;
    private DatePicker birth;

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        drawer = findViewById(R.id.drawer_layout_settings);
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
        setup();
        setupButtons();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                break;

            case R.id.nav_goals:
                startActivity(new Intent(SettingsActivity.this, GoalsActivity.class));
                break;

            case R.id.nav_view_analysis:
                startActivity(new Intent(SettingsActivity.this, ViewAnalysisActivity.class));
                break;

            case R.id.nav_view_add_weight:
                startActivity(new Intent(this, AddWeightActivity.class));
                break;

            case R.id.nav_settings:
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_logout:
                DBConnector connector = DBConnector.getInstance();
                connector.logout();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
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

    private void setup()
    {
        //get the edit texts
        //set them to the right values
        Sys sys = Sys.getInstance();
        String[] data = sys.getUserInfo();
        double[] data2 = sys.getUserHealthData();
        fName = findViewById(R.id.editFirstName);
        lName = findViewById(R.id.editLastName);
        weight = findViewById(R.id.nonEditWeight);
        height = findViewById(R.id.editHeight);
        fName.setText(data[0]);
        lName.setText(data[1]);
        weight.setText(Double.toString(data2[0]));
        height.setText(Double.toString(data2[1]));

        //sort the two spinners
        sex = findViewById(R.id.editSex);
        actLevel = findViewById(R.id.editActivity);
        if(data[2].equals("male"))
        {
            sex.setSelection(0);
        }
        else
        {
            sex.setSelection(1);
        }
        actLevel.setSelection((int) data2[4]);

        //sort the datepicker
        birth = findViewById(R.id.editBirth);
        int[] currentBirth = sys.getUserBirthArray();
        birth.init(currentBirth[2], currentBirth[1], currentBirth[0], null);
    }

    private void setupButtons()
    {
        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        goalButton = (Button) findViewById(R.id.goToGoalButton);
        goalButton.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if(view == cancelButton)
        {
            finish(); //should just go back to previous activity
        }
        if(view == goalButton)
        {
            //will launch new activity to edit the main goal
            startActivity(new Intent(this, EditMainGoalActivity.class));
        }
        if(view == saveButton)
        {
            //do the checks
            if(!checkInput()) //something is wrong so don't upload the info
            {
                return;
            }
            Log.d(TAG, "onClick: save button pressed");
            //uploads the info to the database
            int day = birth.getDayOfMonth();
            int month = birth.getMonth();
            int year = birth.getYear();
            Sys sys = Sys.getInstance();
            sys.setUser(false, fName.getText().toString(), lName.getText().toString(), sex.getSelectedItem().toString(), weight.getText().toString(),
                    height.getText().toString(), actLevel.getSelectedItem().toString(), day, month, year);
            finish();
        }
    }

    private boolean checkInput()
    {
        Sys sys = Sys.getInstance();
        if(sys.checkForNumber(fName.getText().toString()))
        {
            Toast.makeText(this, "There is a number in your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(sys.checkForNumber(lName.getText().toString()))
        {
            Toast.makeText(this, "There is a number in your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        String weightStr = weight.getText().toString().trim();
        double weightNum = Sys.convertToDouble(weightStr);
        if(weightNum > 200 || weightNum < 40)
        {
            Toast.makeText(this, "Seems like an impossible weight...", Toast.LENGTH_SHORT).show();
            return false;
        }
        String heightStr = height.getText().toString().trim();
        double heightNum = Sys.convertToDouble(heightStr);
        if(heightNum > 2.2 || heightNum < 1)
        {
            Toast.makeText(this, "Seems like an impossible height...", Toast.LENGTH_SHORT).show();
            return false;
        }
        //need to have the age checker working properly before adding this constraint
        return true;
    }
}