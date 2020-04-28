package com.example.alphafit.android;

import android.app.Dialog;
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
import android.widget.Toast;

import com.example.alphafit.DBConnector;
import com.example.alphafit.Ingredient;
import com.example.alphafit.Meal;
import com.example.alphafit.MealList;
import com.example.alphafit.listener.OnAddIngredientListener;
import com.example.alphafit.recyclerview.ListItem;
import com.example.alphafit.recyclerview.MyAdapter;
import com.example.alphafit.R;
import com.example.alphafit.Sys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;


public class RecommendMealsFragment extends Fragment implements OnCompleteListener<QuerySnapshot>, View.OnClickListener, MyAdapter.OnItemClickListener{
    private Random rand = new Random();
    private MealList Ml = new MealList();
    private Meal meal;
    private  ArrayList <Meal> rec= new ArrayList <Meal>();

    private ArrayList<Ingredient> ingredientList;
    private Button searchButton, popupAdd;
    private EditText searchBox;
    private View view;

    private ArrayList<ListItem> listItems;
    private OnAddIngredientListener listener;
    private ListItem currentItem;
    private Dialog dialog;

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "SearchIngredientsRecFragme";

    public void setListener (OnAddIngredientListener l)
    {
        listener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recommend_meals, container, false);
        searchButton = view.findViewById(R.id.buttonRM);
        searchButton.setOnClickListener(this);
        ArrayList<ListItem> empty = new ArrayList<>();
        //set up recycler
        recyclerView = view.findViewById(R.id.ListM);
        layoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new MyAdapter(empty);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return view;

    }

    private void setupRecyclerView() {

        Ingredient in;
        listItems = new ArrayList<>();
        for (int i = 0; i < ingredientList.size(); i++) {
            in = ingredientList.get(i);
            System.out.println(in);
            meal = new Meal(in.getName(), "All", (float)in.getWeight(),
                    in.getCalories(), in.getCalories() ) ;
            System.out.println(meal);
            meal.addIngredient(in);
            meal.getTotalCalories();
            meal.getTotalWeight();
            meal.getValue();
            Ml.AddMeal(meal);;
        }


        Sys sys = Sys.getInstance();
        rec = Ml.GetRecommendation(sys.getDisplayInfo()[0] - (sys.getDisplayInfo()[1] - sys.getDisplayInfo()[2]), "All");


        for (int i=0;i < rec.size(); i++){
            listItems.add(Sys.convertToListItem(rec.get(i)));
        }
        mAdapter = new MyAdapter(listItems);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            //then switch it to the ingredients
            DBConnector connector = DBConnector.getInstance();
            for (QueryDocumentSnapshot document : task.getResult()) {
                ingredientList.add(connector.convertToIngredient(document));
            }
            setupRecyclerView();
        } else {
            //set a text view below recycler view declaring the error to visible
            //this will need to be reset every time the search button is pressed
        }
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
        if (view == searchButton) {

            ingredientList = new ArrayList<>();

            DBConnector connector = DBConnector.getInstance();
            connector.getIngredients(this);
        }

        if(view == popupAdd)
        {
            //need to get the new weight value from the dialog
            EditText weightBox = dialog.findViewById(R.id.popup_weight_box);
            String weightStr = weightBox.getText().toString();
            double weight = 0;
            weight = Sys.convertToDouble(weightStr);
            if(weight == -1)
            {
                //wasn't a suitable input maybe a toast error message can go here
                return;
            }
            //get the old weight, find the difference and scale the calories
            double oldWeight = currentItem.getWeight();
            double scaleFactor = weight/oldWeight;
            double calories = currentItem.getCalories() * scaleFactor;
            //pass the information (as an ingredient) back to the main activity
            Ingredient ingredient = new Ingredient(currentItem.getTextName(), weight, (int)calories);
            //then that can add the new ingredient to the meal
            listener.addedIngredient(ingredient);
            dialog.hide();
        }
    }



    public RecommendMealsFragment() {
        // Required empty public constructor
    }



}
