package com.example.alphafit.android;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.alphafit.CardioExercise;
import com.example.alphafit.DBConnector;
import com.example.alphafit.Exercise;
import com.example.alphafit.listener.OnAddedExerciseListener;
import com.example.alphafit.StrengthExercise;
import com.example.alphafit.recyclerview.ListExerciseItem;
import com.example.alphafit.recyclerview.MyExerciseAdapter;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OwnExercisesFragment extends Fragment implements View.OnClickListener, OnCompleteListener<QuerySnapshot>, MyExerciseAdapter.OnItemClickListener {
    private View view;
    private RecyclerView recyclerView;
    private MyExerciseAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button cardioButton, strengthButton, popupAdd;
    
    private ArrayList<Exercise> exerciseList;
    private ArrayList<ListExerciseItem> listItems;
    private OnAddedExerciseListener listener;

    private ListExerciseItem currentItem;
    private Dialog dialog;

    private static final String TAG = "OwnExercisesFragment";

    public void setListener(OnAddedExerciseListener l)
    {
        listener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_own_exercises, container, false);
        cardioButton = view.findViewById(R.id.new_cardio_button);
        strengthButton = view.findViewById(R.id.new_strength_button);
        cardioButton.setOnClickListener(this);
        strengthButton.setOnClickListener(this);

        exerciseList = new ArrayList<>();
        setupRecyclerView();
        loadExercises();
        return view;
    }
    
    public void onClick(View v)
    {
        if(v == cardioButton)
        {
            Intent intent = new Intent(view.getContext(), AddCardioActivity.class);
            startActivity(intent);
        }
        if(v == strengthButton)
        {
            Intent intent = new Intent(view.getContext(), AddStrengthActivity.class);
            startActivity(intent);
        }
        if(v == popupAdd)
        {
            String name = currentItem.getName();
            String type = currentItem.getType();
            EditText durationBox = dialog.findViewById(R.id.popup_duration_box);
            int duration = Sys.convertToInt(durationBox.getText().toString());
            int newCal = 0;
            Exercise e;
            if(type.equals("cardio"))
            {
                EditText distanceBox = dialog.findViewById(R.id.popup_distance_box);
                double distance = Sys.convertToDouble(distanceBox.getText().toString());
                int currentCal = currentItem.getCalories();
                double normDist = currentItem.getRepOrDistance();
                double normDur = currentItem.getDuration();
                double distDiff = distance/normDist;
                double durDiff = duration/normDur;
                newCal = (int) (currentCal * distDiff * durDiff);
                e = new CardioExercise(name, newCal, duration, distance);
            }
            else
            {
                EditText repsBox = dialog.findViewById(R.id.popup_reps_box);
                int reps = Sys.convertToInt(repsBox.getText().toString());
                double normDur = currentItem.getDuration();
                double durDiff = duration/normDur;
                int currentCal = currentItem.getCalories();
                newCal = (int) (currentCal * durDiff);
                e = new StrengthExercise(name, newCal, duration, reps);
            }
            //then add it to the workout list
            listener.addedExercise(e);
            dialog.hide();
        }
    }

    private void setupRecyclerView()
    {
        listItems = new ArrayList<>();
        for(int i=0; i<exerciseList.size(); i++)
        {
            listItems.add(Sys.convertToListExerciseItem(exerciseList.get(i)));
            recyclerView = view.findViewById(R.id.own_recycler_view);
            layoutManager = new LinearLayoutManager(view.getContext());
            mAdapter = new MyExerciseAdapter(listItems);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        }
    }

    private void loadExercises()
    {
        exerciseList = new ArrayList<>();
        DBConnector connector = DBConnector.getInstance();
        connector.getOwnExercises(this);
    }

    @Override
    public void onItemClick(int position)
    {
        //get the listItem
        currentItem = listItems.get(position);
        dialog = new Dialog(view.getContext());
        if(currentItem.getType().equals("cardio"))
        {
            dialog.setContentView(R.layout.popup_cardio_new);
            TextView name = dialog.findViewById(R.id.popup_name);
            name.setText(currentItem.getName());
            TextView calories = dialog.findViewById(R.id.popup_calories);
            calories.setText(currentItem.getInfo());
            EditText duration =  dialog.findViewById(R.id.popup_duration_box);
            duration.setText(Double.toString(currentItem.getDuration()));
            EditText distance = dialog.findViewById(R.id.popup_distance_box);
            distance.setText(Double.toString(currentItem.getRepOrDistance()));
        }
        else
        {
            dialog.setContentView(R.layout.popup_strength_new);
            TextView name = dialog.findViewById(R.id.popup_name);
            name.setText(currentItem.getName());
            TextView calories = dialog.findViewById(R.id.popup_calories);
            calories.setText(currentItem.getInfo());
            EditText duration =  dialog.findViewById(R.id.popup_duration_box);
            duration.setText(Double.toString(currentItem.getDuration()));
            EditText reps = dialog.findViewById(R.id.popup_reps_box);
            reps.setText(Double.toString(currentItem.getRepOrDistance()));
        }
        //setup of the button
        popupAdd = dialog.findViewById(R.id.popup_add);
        popupAdd.setOnClickListener(this);

        //show the popup
        dialog.setCancelable(true);
        dialog.show();
    }

    public void onComplete(@NonNull Task<QuerySnapshot> task)
    {
        if(task.isSuccessful())
        {
            exerciseList = new ArrayList<>();
            DBConnector connector = DBConnector.getInstance();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Log.d(TAG, "onComplete: found exercise");
                exerciseList.add(connector.convertToExercise(document));
            }
            setupRecyclerView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
        loadExercises();
    }
}