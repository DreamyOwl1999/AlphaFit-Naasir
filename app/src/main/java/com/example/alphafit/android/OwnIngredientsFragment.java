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

import com.example.alphafit.DBConnector;
import com.example.alphafit.Ingredient;
import com.example.alphafit.recyclerview.ListItem;
import com.example.alphafit.recyclerview.MyAdapter;
import com.example.alphafit.listener.OnAddIngredientListener;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class OwnIngredientsFragment extends Fragment implements MyAdapter.OnItemClickListener, OnCompleteListener<QuerySnapshot>, View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Dialog dialog;
    private ListItem currentItem;
    private Button popupAdd, newIngredientButton;
    private OnAddIngredientListener listener;

    private ArrayList<Ingredient> ingredientList;
    private ArrayList<ListItem> listItems;

    private static final String TAG = "OwnIngredientsFragment";

    public void setListener(OnAddIngredientListener l)
    {
        listener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_own_ingredients, container, false);
        newIngredientButton = view.findViewById(R.id.new_ing_button);
        newIngredientButton.setOnClickListener(this);
        ingredientList = new ArrayList<>();
        setupRecyclerView();
        loadIngredients();

        return view;
    }

    public void setupRecyclerView()
    {
        listItems = new ArrayList<>();
        for (int i = 0; i < ingredientList.size(); i++) {
            listItems.add(Sys.convertToListItem(ingredientList.get(i)));
        }
        Log.d(TAG, "setupRecyclerView: list numbers " + listItems.size());
        recyclerView = view.findViewById(R.id.own_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new MyAdapter(listItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    public void onItemClick(int position)
    {
        //get the listItem
        currentItem = listItems.get(position);
        //open up the dialog stuff from here
        dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.popup);
        //change the texts to the right ones
        TextView name = dialog.findViewById(R.id.popup_name);
        name.setText(currentItem.getTextName());
        TextView calories = dialog.findViewById(R.id.popup_calories);
        calories.setText(currentItem.getCalories() + " calories per " + currentItem.getWeight());
        EditText weight =  dialog.findViewById(R.id.popup_weight_box);
        weight.setText(Double.toString(currentItem.getWeight()));

        //setup of the button
        popupAdd = dialog.findViewById(R.id.popup_add);
        popupAdd.setOnClickListener(this);

        //show the popup
        dialog.setCancelable(true);
        dialog.show();
    }

    public void onClick(View view) {
        if (view == popupAdd) {
            //only button is add
            //need to get the new weight value from the dialog
            EditText weightBox = dialog.findViewById(R.id.popup_weight_box);
            String weightStr = weightBox.getText().toString();
            double weight = 0;
            weight = Sys.convertToDouble(weightStr);
            if (weight == -1) {
                //wasn't a suitable input maybe a toast error message can go here
                return;
            }
            //get the old weight, find the difference and scale the calories
            double oldWeight = currentItem.getWeight();
            double scaleFactor = weight / oldWeight;
            double calories = currentItem.getCalories() * scaleFactor;
            //pass the information (as an ingredient) back to the main activity
            Ingredient ingredient = new Ingredient(currentItem.getTextName(), weight, (int) calories);
            //then that can add the new ingredient to the meal
            listener.addedIngredient(ingredient);
            dialog.hide();
        }

        if(view == newIngredientButton)
        {
            Intent intent = new Intent(view.getContext(), AddIngredientActivity.class);
            startActivity(intent);
        }
    }

    private void loadIngredients()
    {
        ingredientList = new ArrayList<>();
        DBConnector connector = DBConnector.getInstance();
        connector.getOwnIngredients(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            //then switch it to the ingredients
            ingredientList = new ArrayList<>();
            DBConnector connector = DBConnector.getInstance();
            for (QueryDocumentSnapshot document : task.getResult())
            {
                Log.d(TAG, "onComplete: found ingredient");
                ingredientList.add(connector.convertToIngredient(document));
            }
            //then do the rest of the recycler view setup
            setupRecyclerView();
        } else {
            //set a text view below recycler view declaring the error to visible
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
        loadIngredients();
    }
}