package com.example.alphafit.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphafit.Almanac;
import com.example.alphafit.DBConnector;
import com.example.alphafit.Meal;
import com.example.alphafit.Task;
import com.example.alphafit.Tetris_UI;
import com.example.alphafit.Workout;
import com.example.alphafit.recyclerview.HomePageAdapter;
import com.example.alphafit.recyclerview.ListHomeItem;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.util.ArrayList;

/***
 * Activity class displaying the user's diary information from which they can navigate to adding meals and workouts
 * Contains main menu
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomePageAdapter.OnItemClickListener, View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    private DrawerLayout drawer;
    private static final String TAG = "HomeActivity";
    private ArrayList<ListHomeItem> listHomeItems;

    private RecyclerView recyclerView;
    private HomePageAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ImageButton leftArrow, rightArrow;
    private Button dateButton, scheduleBreakfast, scheduleLunch, scheduleDinner, scheduleSnacks, scheduleWorkout, planner;
    private DatePickerDialog dateDialog;

    ArrayList<Task> scheduledTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setCollapseIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        scheduledTasks = new ArrayList<>();

        Log.d(TAG, "onCreate: STARTING THE ACTIVITY");
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //get the text view
        View header = navigationView.getHeaderView(0);
        TextView welcome = (TextView) header.findViewById(R.id.welcome_user);
        //find the email from db
        Log.d(TAG, "onCreate: loading the user");
        Sys sys = Sys.getInstance();
        welcome.setText("Welcome " + sys.getName());
        if(!sys.checkHomePageReady())
        {
            sys.initialiseHomePage();
        }
        sys.setHomeActivity(this);
        //set up the important information
        setupTopInfo();
        setupRecyclerView();
        setupButtons();
        sys.loadDatabaseMeals();
        sys.loadDatabaseWorkouts();
    }

    public void launch(String type, String type2) //one for meal or workout, one for type of meal
    {
        if(type.equals("meal"))
        {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("MEAL", type2);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, AddWorkoutActivity.class);
            intent.putExtra("TYPE", -1);
            startActivity(intent);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setupTopInfo();
        setupRecyclerView();
    }

    private void setupButtons()
    {
        Sys sys = Sys.getInstance();
        leftArrow = findViewById(R.id.left_arrow);
        rightArrow = findViewById(R.id.right_arrow);
        dateButton = findViewById(R.id.clickable_date);
        dateButton.setText(sys.getNiceDate());
        leftArrow.setOnClickListener(this);
        rightArrow.setOnClickListener(this);
        dateButton.setOnClickListener(this);


        planner = findViewById(R.id.schedule_planner);
        planner.setOnClickListener(this);

        scheduleBreakfast = findViewById(R.id.schedule_breakfast);
        scheduleBreakfast.setOnClickListener(this);
        scheduleLunch = findViewById(R.id.schedule_lunch);
        scheduleLunch.setOnClickListener(this);
        scheduleDinner = findViewById(R.id.schedule_dinner);
        scheduleDinner.setOnClickListener(this);
        scheduleSnacks = findViewById(R.id.schedule_snacks);
        scheduleSnacks.setOnClickListener(this);
        scheduleWorkout = findViewById(R.id.schedule_workout);
        scheduleWorkout.setOnClickListener(this);
    }

    private void setupRecyclerView()
    {
        Sys sys = Sys.getInstance();
        //setup the list
        listHomeItems = new ArrayList<>();
        //need a new item for each meal
        listHomeItems.add(Sys.convertToListHomeItemFromMeal(sys.getMeal("breakfast")));
        listHomeItems.add(Sys.convertToListHomeItemFromMeal(sys.getMeal("lunch")));
        listHomeItems.add(Sys.convertToListHomeItemFromMeal(sys.getMeal("dinner")));
        listHomeItems.add(Sys.convertToListHomeItemFromMeal(sys.getMeal("snacks")));

        //add the workout information
        for(int i=0; i<sys.getNoWorkouts(); i++)
        {
            listHomeItems.add(Sys.convertToListHomeItemFromWorkout(sys.getSelectedWorkout(i)));
        }
        boolean check = true;
        for(int i=0; i<listHomeItems.size(); i++)
        {
            if(!listHomeItems.get(i).getCompleted())
            {
                check = false;
            }
        }
        if(check)
        {
            //then all items completed for the day
            new AlertDialog.Builder(this)
                    .setMessage("Congratulations! You have completed all activities for today")
                    .setPositiveButton("OK", null).show();
        }
        //and then add a 'new' workout item at the bottom
        listHomeItems.add(new ListHomeItem("New Workout", 0, "", false, ""));

        //setup recycler view
        recyclerView = findViewById(R.id.home_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new HomePageAdapter(listHomeItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void setupTopInfo()
    {
        Sys sys = Sys.getInstance();
        int[] info = sys.getDisplayInfo();
        TextView burned = findViewById(R.id.calories_intake);
        TextView eaten = findViewById(R.id.calories_eaten);
        TextView exercise = findViewById(R.id.calories_exercise);
        TextView total = findViewById(R.id.calories_total);
        burned.setText(Integer.toString(info[0]));
        exercise.setText(Integer.toString(info[2]));
        eaten.setText(Integer.toString(info[1]));
        int calcTotal = info[0] + info[2] - info[1]; //burned + exercise - eaten
        total.setText(Integer.toString(calcTotal));
        checkForScheduledTasks();
    }

    private void checkForScheduledTasks()
    {
        scheduledTasks = new ArrayList<>();
        Sys sys = Sys.getInstance();
        scheduledTasks = sys.getScheduledTasks(sys.getSelectedDate());
    }

    private Task getScheduledTask(String type, String type2)
    {
        for(int i=0; i<scheduledTasks.size(); i++)
        {
            Task temp = scheduledTasks.get(i);
            if(type.equals("meal")) {
                if (temp instanceof Meal) {
                    if(type2.equals(((Meal) temp).getMealType()))
                    {
                        return temp;
                    }
                }
            }
            if(type.equals("workout"))
            {
                if(temp instanceof Workout)
                {
                    return temp;
                }
            }
        }
        return null;
    }

    @Override
    public void onItemClick(int position, int listSize)
    {
        LocalDate today = LocalDate.now();
        if(Sys.getInstance().getSelectedDate().isAfter(today))
        {
            Toast.makeText(this, "Woops! You can't add items to the future", Toast.LENGTH_SHORT);
            return;
        }
        if(position < 4)
        {
            //then a meal was clicked so launch that activity
            Intent intent = new Intent(this, AddMealActivity.class);
            if(position == 0)
            {
                //first check if there is a scheduled breakfast
                Task task = getScheduledTask("meal", "breakfast");
                Sys sys = Sys.getInstance();
                sys.setCurrentTask(task);
                if(task == null) {
                    intent.putExtra("MEAL", "breakfast");
                    startActivity(intent);
                }
                else
                {
                    //launch dialog for asking
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Prescheduled Breakfast");
                    builder.setMessage("You have a prescheduled breakfast in place. Would you like to add it to your diary?");
                    builder.setPositiveButton("YES", Sys.getInstance());
                    builder.setNegativeButton("NO", Sys.getInstance());
                    builder.show();
                }
            }
            if(position == 1)
            {
                //first check if there is a scheduled lunch
                Task task = getScheduledTask("meal", "lunch");
                Sys sys = Sys.getInstance();
                sys.setCurrentTask(task);
                if(task == null) {
                    intent.putExtra("MEAL", "lunch");
                    startActivity(intent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Prescheduled Lunch");
                    builder.setMessage("You have a prescheduled lunch in place. Would you like to add it to your diary?");
                    builder.setPositiveButton("YES", Sys.getInstance());
                    builder.setNegativeButton("NO", Sys.getInstance());
                    builder.show();
                }
            }
            if(position == 2)
            {
                //first check if there is a scheduled dinner
                Task task = getScheduledTask("meal", "dinner");
                Sys sys = Sys.getInstance();
                sys.setCurrentTask(task);
                if(task == null) {
                    intent.putExtra("MEAL", "dinner");
                    startActivity(intent);
                }
                else
                {
                    //launch dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Prescheduled Dinner");
                    builder.setMessage("You have a prescheduled dinner in place. Would you like to add it to your diary?");
                    builder.setPositiveButton("YES", Sys.getInstance());
                    builder.setNegativeButton("NO", Sys.getInstance());
                    builder.show();
                }
            }
            if(position == 3)
            {
                //first check if there is a scheduled snack
                Task task = getScheduledTask("meal", "snacks");
                Sys sys = Sys.getInstance();
                sys.setCurrentTask(task);
                if(task == null)
                {
                    intent.putExtra("MEAL", "snacks");
                    startActivity(intent);
                }
                else
                {
                    //launch dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Prescheduled Snacks");
                    builder.setMessage("You have a prescheduled snack in place. Would you like to add it to your diary?");
                    builder.setPositiveButton("YES", Sys.getInstance());
                    builder.setNegativeButton("NO", Sys.getInstance());
                    builder.show();
                }
            }
        }
        else
        {
            //a workout was clicked so launch that activity
            Intent intent = new Intent(this, AddWorkoutActivity.class);
            if((listSize - position) == 1)
            {
                //first check if there is a scheduled workout
                Task task = getScheduledTask("workout", "");
                Sys sys = Sys.getInstance();
                sys.setCurrentTask(task);
                if(task == null) { //no scheduled workout so go straight to new workout
                    //it's the last item in the list so a new workout
                    intent.putExtra("TYPE", -1);
                    startActivity(intent);
                }
                else
                {
                    //launch dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Prescheduled Workout");
                    builder.setMessage("You have a prescheduled workout in place. Would you like to add it to your diary?");
                    builder.setPositiveButton("YES", Sys.getInstance());
                    builder.setNegativeButton("NO", Sys.getInstance());
                    builder.show();
                }
            }
            else
            {
                //it's a current workout not a new one
                intent.putExtra("TYPE", listSize-5);
                startActivity(intent);
            }

        }
    }

    public void onClick(View view)
    {
        if(view == leftArrow)
        {
            //then go back one day
            Sys sys = Sys.getInstance();
            sys.changeDay(false);
            //need to update the recycler view with the new information
            setupTopInfo();
            setupRecyclerView();
            //need to update the text in dateButton to display the right day
            dateButton.setText(sys.getNiceDate());
        }
        if(view == rightArrow)
        {
            //then go forward one day
            Sys sys = Sys.getInstance();
            sys.changeDay(true);
            //need to update the recycler view with the new information
            setupTopInfo();
            setupRecyclerView();
            //need to update the text in dateButton to display the right day
            dateButton.setText(sys.getNiceDate());
        }
        if(view == dateButton)
        {
            //then show the date picker dialog
            dateDialog = new DatePickerDialog(this);
            dateDialog.setOnDateSetListener(this);
            dateDialog.setCancelable(true);
            dateDialog.show();
        }

        if(view == planner){
            Intent intent = new Intent(this, Tetris_UI.class);
            startActivity(intent);
        }

        if(view == scheduleBreakfast)
        {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("MEAL", "breakfast");
            intent.putExtra("SCHEDULED", true);
            startActivity(intent);
        }
        if(view == scheduleLunch)
        {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("MEAL", "lunch");
            intent.putExtra("SCHEDULED", true);
            startActivity(intent);
        }
        if(view == scheduleDinner)
        {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("MEAL", "dinner");
            intent.putExtra("SCHEDULED", true);
            startActivity(intent);
        }
        if(view == scheduleSnacks)
        {
            Intent intent = new Intent(this, AddMealActivity.class);
            intent.putExtra("MEAL", "snacks");
            intent.putExtra("SCHEDULED", true);
            startActivity(intent);
        }
        if(view == scheduleWorkout)
        {
            Intent intent = new Intent(this, AddWorkoutActivity.class);
            intent.putExtra("SCHEDULED", true);
            startActivity(intent);
        }
        if(view == planner){
            Almanac al = new Almanac();
            int j = al.getItemListLength();
            if(j!=0){
                Intent intent = new Intent(this, Tetris_UI.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "No Items Scheduled", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_goals:
                startActivity(new Intent(this, GoalsActivity.class));
                break;

            case R.id.nav_view_analysis:
                startActivity(new Intent(this, ViewAnalysisActivity.class));
                break;

            case R.id.nav_view_add_weight:
                startActivity(new Intent(this, AddWeightActivity.class));
                break;

            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Log.d(TAG, "onDateSet: month val is " + month);
        Sys sys = Sys.getInstance();
        boolean checker = sys.changeDaySpecific(dayOfMonth, month, year);
        dateDialog.hide();
        if(!checker)
        {
            Toast.makeText(this, "Picked an invalid date", Toast.LENGTH_SHORT).show();
        }
        else
        {
            setupTopInfo();
            setupRecyclerView();
            //need to update the text in dateButton to display the right day
            dateButton.setText(sys.getNiceDate());
        }
    }
}