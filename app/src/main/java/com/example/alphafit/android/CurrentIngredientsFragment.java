package com.example.alphafit.android;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alphafit.DBConnector;
import com.example.alphafit.Ingredient;
import com.example.alphafit.recyclerview.ListItem;
import com.example.alphafit.Meal;
import com.example.alphafit.recyclerview.MyAdapter;
import com.example.alphafit.R;
import com.example.alphafit.Sys;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CurrentIngredientsFragment extends Fragment implements View.OnClickListener, MyAdapter.OnItemClickListener
{
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Meal meal;
    private View view;
    private Dialog dialog;
    private int currentPosition;

    TimePicker timePicker;
    private Button cancelButton, saveButton, deletePopup, savePopup;
    private ArrayList<ListItem> listItems;
    private Communicator communicator;
    private boolean scheduled;

    private static final String TAG = "CurrentIngredientsFragm";

    public interface Communicator
    {
        public Meal getMeal();
        public void stopActivity();
    }

    public void setScheduled(boolean b)
    {
        scheduled = b;
    }

    public void setCommunicator(Communicator c)
    {
        communicator = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        currentPosition = -1;
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_current_ingredients, container, false);
        setupButtons();

        //get the meal
        meal = communicator.getMeal();
        setupInfo();

        //make the list of ingredients
        updateListItems();

        //set up recycler view
        recyclerView = view.findViewById(R.id.current_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new MyAdapter(listItems);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        timePicker = view.findViewById(R.id.meal_time);

        return view;
    }

    private void setupButtons()
    {
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton = view.findViewById(R.id.save_meal_button);
        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    private void setupInfo()
    {
        TextView totalWeightBox = view.findViewById(R.id.meal_weight);
        TextView totalCaloriesBox = view.findViewById(R.id.meal_calories);
        totalWeightBox.setText("Total Weight: " + Float.toString(meal.getTotalWeight()));
        totalCaloriesBox.setText("Total Calories: " + Integer.toString(meal.getTotalCalories()));
    }

    public void onClick(View view)
    {
        if(view == cancelButton)
        {
            communicator.stopActivity();
        }
        if(view == saveButton)
        {
            //save the meal to the right variable in day
            Log.d(TAG, "onClick: FINISHED time to save");

            LocalDate date = Sys.getInstance().getSelectedDate();
            int hours = timePicker.getHour();
            int minutes = timePicker.getMinute();
            LocalTime time = LocalTime.of(hours, minutes);
            LocalDateTime timeChosen = LocalDateTime.of(date, time);
            meal.setTime(timeChosen);
            if(meal.getTotalCalories() <= 0)
            {
                Toast.makeText(this.getContext(), "You can't have an empty meal", Toast.LENGTH_SHORT).show();
                return;
            }
            meal.updateCompleted(true);
            Sys sys = Sys.getInstance();
            sys.addNewMeal(meal, meal.getMealType(), sys.getSelectedDate(), scheduled);
            if(!scheduled) {
                DBConnector connector = DBConnector.getInstance();
                connector.uploadMeal(meal);
            }
            communicator.stopActivity();
        }
        if(view == savePopup)
        {
            //save the new values for the ingredients
            //get the new values
            EditText weightBox = dialog.findViewById(R.id.popup_weight_box);
            String newWeightStr = weightBox.getText().toString();
            double newWeight = Sys.convertToDouble(newWeightStr);
            if(newWeight == -1)
            {
                //something went wrong
                return;
            }
            ListItem temp = listItems.get(currentPosition);
            double oldWeight = temp.getWeight();
            int newCalories = (int) (temp.getCalories() * (newWeight/oldWeight));
            Ingredient newIngredient = new Ingredient(temp.getTextName(), newWeight, newCalories);
            //remove the old ingredient
            meal.replaceIngredient(currentPosition, newIngredient);
            updateListItems();
            mAdapter = new MyAdapter(listItems);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
            dialog.hide();
        }
        if(view == deletePopup)
        {
            //delete the ingredient from the list
            //why does meal have no ingredients?
            Log.d(TAG, "onClick: meal size" + meal.getIngredients().size());
            meal.removeIngredient(currentPosition);
            updateListItems();
            mAdapter = new MyAdapter(listItems);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
            dialog.hide();
        }
    }

    @Override
    public void onItemClick(int position)
    {
        Log.d(TAG, "onItemClick: this works");
        ListItem currentItem = listItems.get(position);
        currentPosition = position;
        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.popup_current);
        //set the text views
        TextView name = dialog.findViewById(R.id.popup_name);
        name.setText(currentItem.getTextName());
        EditText weight = dialog.findViewById(R.id.popup_weight_box);
        weight.setText(Double.toString(currentItem.getWeight()));
        TextView calories = dialog.findViewById(R.id.popup_calories);
        calories.setText(currentItem.getCalories() + " calories per " + currentItem.getWeight());
        //setup the buttons
        deletePopup = dialog.findViewById(R.id.popup_delete);
        savePopup = dialog.findViewById(R.id.popup_edit);
        deletePopup.setOnClickListener(this);
        savePopup.setOnClickListener(this);
        //make dialog visible
        dialog.setCancelable(true);
        dialog.show();
    }

    public void passBackIngredient(Ingredient ingredient)
    {
        meal.addIngredient(ingredient);
        Log.d(TAG, "passBackMeal: meal.size" +  meal.getIngredients().size());
        updateListItems();
        mAdapter = new MyAdapter(listItems);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        setupInfo();
    }

    public void updateListItems()
    {
        listItems = new ArrayList<>();
        ArrayList<Ingredient> temp = meal.getIngredients();
        for(int i=0; i<temp.size(); i++)
        {
            listItems.add(Sys.convertToListItem(temp.get(i)));
        }
    }
}