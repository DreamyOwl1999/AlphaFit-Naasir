package com.example.alphafit.android.goalfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.goals.StreakGoal;

import java.time.LocalDate;

/**
 * Fragment for creating a streak goal in NewGoalActivity
 */

public class StreakGoalFragment extends Fragment implements View.OnClickListener
{
    private Button saveButton;
    private CheckBox boxMon, boxTue, boxWed, boxThu, boxFri, boxSat, boxSun;
    private DatePicker start, end;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_streak_goal, container, false);
        saveButton = view.findViewById(R.id.save_goal_button);
        saveButton.setOnClickListener(this);
        boxMon = view.findViewById(R.id.check_mon);
        boxTue = view.findViewById(R.id.check_tue);
        boxWed = view.findViewById(R.id.check_wed);
        boxThu = view.findViewById(R.id.check_thu);
        boxFri = view.findViewById(R.id.check_fri);
        boxSat = view.findViewById(R.id.check_sat);
        boxSun = view.findViewById(R.id.check_sun);
        start = view.findViewById(R.id.picker_start);
        end = view.findViewById(R.id.picker_end);

        return view;
    }

    public void onClick(View v)
    {
        if(v == saveButton)
        {
            boolean[] daysOfWeek = new boolean[7];
            daysOfWeek[0] = boxMon.isChecked();
            daysOfWeek[1] = boxTue.isChecked();
            daysOfWeek[2] = boxWed.isChecked();
            daysOfWeek[3] = boxThu.isChecked();
            daysOfWeek[4] = boxFri.isChecked();
            daysOfWeek[5] = boxSat.isChecked();
            daysOfWeek[6] = boxSun.isChecked();
            StreakGoal newGoal = getGoal(daysOfWeek);
            if(newGoal == null)
            {
                return;
            }
            Sys.getInstance().addGoal(newGoal);
            getActivity().finish();
        }
    }

    private StreakGoal getGoal(boolean[] days)
    {
        LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth()+1, start.getDayOfMonth());
        LocalDate endDate = LocalDate.of(end.getYear(), end.getMonth()+1, end.getDayOfMonth());
        if(startDate.isAfter(endDate))
        {
            return null;
        }
        StreakGoal newGoal = new StreakGoal(startDate, endDate, days);
        return newGoal;
    }

}