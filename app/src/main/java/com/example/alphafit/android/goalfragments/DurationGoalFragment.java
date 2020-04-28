package com.example.alphafit.android.goalfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.goals.CalorieGoal;
import com.example.alphafit.goals.DurationGoal;

import java.time.LocalDate;

import javax.xml.datatype.Duration;

/**
 * Fragment for adding a duration goal in NewGoalActivity
 */
public class DurationGoalFragment extends Fragment implements View.OnClickListener
{
    private Button saveButton;
    private DatePicker start, end;
    private EditText durationBox;
    public NewGoalActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_duration_goal, container, false);
        saveButton = view.findViewById(R.id.save_goal_button);
        saveButton.setOnClickListener(this);
        start = view.findViewById(R.id.picker_start);
        end = view.findViewById(R.id.picker_end);
        durationBox = view.findViewById(R.id.duration_goal);

        return view;
    }

    public void onClick(View v)
    {
        if(v == saveButton)
        {
            if(getGoal() == null)
            {
                return;
            }
            Sys.getInstance().addGoal(getGoal());
            getActivity().finish();
        }
    }

    private DurationGoal getGoal()
    {
        LocalDate startDate = LocalDate.of(start.getYear(), start.getMonth()+1, start.getDayOfMonth());
        LocalDate endDate = LocalDate.of(start.getYear(), start.getMonth()+1, start.getDayOfMonth());
        if(startDate.isAfter(endDate))
        {
            return null;
        }
        int duration = Sys.convertToInt(durationBox.getText().toString());
        DurationGoal newGoal = new DurationGoal(startDate, endDate, duration);
        return newGoal;
    }
}