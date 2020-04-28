package com.example.alphafit.android.goalfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.example.alphafit.goals.WeightGoal;

/**
 * Fragment for adding a new weight goal
 */
public class WeightGoalFragment extends Fragment implements View.OnClickListener{
    private Button saveButton;
    private EditText weightBox;
    public NewGoalActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight_goal, container, false);
        saveButton = view.findViewById(R.id.save_goal_button);
        saveButton.setOnClickListener(this);
        weightBox = view.findViewById(R.id.weight_goal);
        return view;
    }

    public void onClick(View v)
    {
        if(v == saveButton)
        {
            Sys.getInstance().addGoal(getGoal());
            getActivity().finish();
        }
    }

    private WeightGoal getGoal()
    {
        String type;
        double newWeight = Sys.convertToDouble(weightBox.getText().toString());
        //check user's weight for goal type
        double userWeight = Sys.getInstance().getUserHealthData()[0];
        if(newWeight < userWeight)
        {
            type = "Lose Weight";
        }
        else
        {
            type = "Gain Weight";
        }
        WeightGoal newGoal = new WeightGoal(newWeight, type);
        return newGoal;
    }
}
