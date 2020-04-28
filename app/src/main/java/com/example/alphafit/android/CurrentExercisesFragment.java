package com.example.alphafit.android;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alphafit.CardioExercise;
import com.example.alphafit.DBConnector;
import com.example.alphafit.Exercise;
import com.example.alphafit.listener.OnAddedExerciseListener;
import com.example.alphafit.StrengthExercise;
import com.example.alphafit.recyclerview.ListExerciseItem;
import com.example.alphafit.recyclerview.MyExerciseAdapter;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.Workout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/***
 * Fragment class used by AddWorkoutActivity to display the current exercises in the workout and other information
 * Allows user to save workout to the system
 */

public class CurrentExercisesFragment extends Fragment implements View.OnClickListener, MyExerciseAdapter.OnItemClickListener, OnAddedExerciseListener
{
    private Communicator communicator;

    private RecyclerView recyclerView;
    private MyExerciseAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Workout workout;
    private View view;

    private ListExerciseItem currentItem;
    private int currentPosition;
    private Dialog dialog;

    private Button saveButton, cancelButton, popupEdit, popupDelete;
    private ArrayList<ListExerciseItem> listItems;
    private EditText nameBox;

    private boolean scheduled;

    public interface Communicator
    {
        public Workout getWorkout();
        public void stopActivity();
        public int getWorkoutPosition();
    }

    public void setCommunicator(Communicator c)
    {
        communicator = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_current_exercises, container, false);
        saveButton = view.findViewById(R.id.save_workout_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        nameBox = view.findViewById(R.id.workout_name);

        workout = communicator.getWorkout();

        updateListItems();
        setupRecyclerview();

        setupInfo();
        currentPosition = -1;
        return view;
    }

    public void setScheduled(boolean b)
    {
        scheduled = b;
    }

    @Override
    public void onClick(View view)
    {
        if(view == saveButton)
        {
            //save the workout here
            Sys sys = Sys.getInstance();
            setWorkoutStats();
            if(workout.getTotalCalories() <= 0)
            {
                Toast.makeText(this.getContext(), "You can't have an empty workout", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = nameBox.getText().toString();
            if(name == "")
            {
                return;
            }
            for(int i=0; i<sys.getNoWorkouts(); i++)
            {
                if(sys.getSelectedWorkout(i).getName().equals(name))
                {
                    Toast.makeText(this.getContext(), "You can't have workouts with the same name for each day.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            workout.setName(name);
            workout.setDocID(name + " " + workout.getDate().toString());
            int pos = communicator.getWorkoutPosition();
            if(pos == -1)
            {
                sys.addNewWorkout(workout, workout.getDate(), scheduled);
            }
            else
            {
                sys.replaceWorkout(workout, pos);
            }
            if(!scheduled) {
                DBConnector connector = DBConnector.getInstance();
                connector.uploadWorkout(workout, workout.getDate());
            }
            //then stop the activity
            communicator.stopActivity();
        }
        if(view == cancelButton)
        {
            //stop the activity, go back to home
            communicator.stopActivity();
        }
        if(view == popupDelete)
        {
            workout.removeExercise(currentPosition);
            updateListItems();
            mAdapter = new MyExerciseAdapter(listItems);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
            dialog.hide();
        }
        if(view == popupEdit)
        {
            Exercise e;
            int newCal = 0;
            EditText durationBox = dialog.findViewById(R.id.popup_duration_box);
            int newDuration = Sys.convertToInt(durationBox.getText().toString());
            double durationDiff = (double)newDuration/(double)currentItem.getDuration();
            if(currentItem.getType().equals("cardio"))
            {
                int currentCal = currentItem.getCalories();
                double oldDistance = currentItem.getRepOrDistance();
                EditText distanceBox = dialog.findViewById(R.id.popup_distance_box);
                double newDistance = Sys.convertToDouble(distanceBox.getText().toString());
                double distanceDiff = newDistance/oldDistance;
                newCal = (int) (currentCal * durationDiff * distanceDiff);
                e = new CardioExercise(currentItem.getName(), newCal, newDuration, newDistance);
            }
            else
            {
                int currentCal = currentItem.getCalories();
                EditText repsBox = dialog.findViewById(R.id.popup_reps_box);
                int reps = Sys.convertToInt(repsBox.getText().toString());
                newCal = (int) (currentCal * durationDiff);
                e = new StrengthExercise(currentItem.getName(), newCal, newDuration, reps);
            }
            workout.replaceExercise(currentPosition, e);
            updateListItems();
            mAdapter = new MyExerciseAdapter(listItems);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
            dialog.hide();
        }
    }

    @Override
    public void onItemClick(int position)
    {
        //make the popup appear here
        currentPosition = position;
        currentItem = listItems.get(position);
        dialog = new Dialog(view.getContext());
        if(currentItem.getType().equals("cardio"))
        {
            dialog.setContentView(R.layout.popup_cardio);
            TextView name = dialog.findViewById(R.id.popup_name);
            name.setText(currentItem.getName());
            EditText duration = dialog.findViewById(R.id.popup_duration_box);
            duration.setText(currentItem.getDuration());
            EditText distance = dialog.findViewById(R.id.popup_distance_box);
            distance.setText(Double.toString(currentItem.getRepOrDistance()));
            TextView calories = dialog.findViewById(R.id.popup_calories);
            calories.setText(currentItem.getInfo());
        }
        else
        {
            dialog.setContentView(R.layout.popup_strength);
            TextView name = dialog.findViewById(R.id.popup_name);
            name.setText(currentItem.getName());
            EditText duration = dialog.findViewById(R.id.popup_duration_box);
            duration.setText(Integer.toString(currentItem.getDuration()));
            EditText reps = dialog.findViewById(R.id.popup_reps_box);
            reps.setText(Double.toString(currentItem.getRepOrDistance()));
            TextView calories = dialog.findViewById(R.id.popup_calories);
            calories.setText(currentItem.getInfo());
        }
        popupEdit = dialog.findViewById(R.id.popup_edit);
        popupDelete = dialog.findViewById(R.id.popup_delete);
        popupEdit.setOnClickListener(this);
        popupDelete.setOnClickListener(this);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void setWorkoutStats()
    {
        TimePicker picker = view.findViewById(R.id.workout_time);
        int hours = picker.getHour();
        int minutes = picker.getMinute();
        LocalDate date = Sys.getInstance().getSelectedDate();
        LocalTime time = LocalTime.of(hours, minutes);
        LocalDateTime timeChosen = LocalDateTime.of(date, time);
        workout.setTime(timeChosen);
        workout.updateCompleted(true);
    }

    public void updateListItems()
    {
        listItems = new ArrayList<>();
        ArrayList<Exercise> temp = workout.getExercises();
        for(int i=0; i<temp.size(); i++)
        {
            listItems.add(Sys.convertToListExerciseItem(temp.get(i)));
        }
    }

    private void setupRecyclerview()
    {
        recyclerView = view.findViewById(R.id.current_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new MyExerciseAdapter(listItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void setupInfo()
    {
        //set up the top info with up to date info (duration and calories)
        int duration = workout.getTotalDuration();
        int calories = workout.getTotalCalories();
        TextView calorieText = view.findViewById(R.id.workout_calories);
        calorieText.setText("Total Calories: " + calories + " calories");
        TextView durationText = view.findViewById(R.id.workout_duration);
        durationText.setText("Total Duration: " + duration + " minutes");
        //set up the name if the workout has one
        if(!workout.getName().equals(""))
        {
          nameBox.setText(workout.getName());
          nameBox.setEnabled(false);
        }

    }

    public void addedExercise(Exercise e)
    {
        workout.addExercise(e);
        updateListItems();
        setupRecyclerview();
        setupInfo();
    }
}