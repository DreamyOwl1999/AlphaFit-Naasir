package com.example.alphafit.android.goalfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.goals.CalorieGoal;

import java.time.LocalDate;

public class CalorieGoalFragment extends Fragment implements View.OnClickListener
{
    private Button saveButton;
    private DatePicker start, end;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calorie_goal, container, false);
        start = view.findViewById(R.id.picker_start);
        end = view.findViewById(R.id.picker_end);
        saveButton = view.findViewById(R.id.save_goal_button);
        saveButton.setOnClickListener(this);
        return view;
    }

    public void onClick(View v)
    {
        if(v == saveButton)
        {
            //create goal from info
            CalorieGoal goal = getGoal();
            if(goal == null)
            {
                return;
            }
            Sys.getInstance().addGoal(goal);
            getActivity().finish();
            //take the steps to save the goal to the list and upload it to the database
        }
    }

    private CalorieGoal getGoal()
    {
        LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth()+1, start.getDayOfMonth());
        LocalDate endDate = LocalDate.of(start.getYear(), start.getMonth()+1, start.getDayOfMonth());
        if(startDate.isAfter(endDate))
        {
            return null;
        }
        CalorieGoal newGoal = new CalorieGoal(startDate, endDate);
        return newGoal;
    }
}