package com.example.alphafit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.alphafit.android.EnterInfoActivity;
import com.example.alphafit.android.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnector
{
    private static DBConnector instance;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private boolean isLoggedIn;

    private static final String TAG = "DBConnector";

    private DBConnector()
    {
        Log.d(TAG, "DBConnector: RESTARTING ------------------");
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        isLoggedIn = false;
    }

    public static DBConnector getInstance()
    {
        if(instance == null)
        {
            instance = new DBConnector();
        }
        return instance;
    }

    public boolean checkLoggedIn() {

        return isLoggedIn;
    }

    public void registerUser(final Activity context, String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    isLoggedIn = true;
                    Log.d(TAG, "TASK: log in/reg succeeded");
                    user = firebaseAuth.getCurrentUser();
                    context.startActivity(new Intent(context, EnterInfoActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateUserWeight(double newWeight)
    {
        DocumentReference doc = db.collection("users").document(user.getEmail());
        Map<String, Object> data = new HashMap<>();
        data.put("weight", newWeight);
        doc.update(data);
    }

    public void login(final Activity context, String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    isLoggedIn = true;
                    Log.d(TAG, "TASK: log in/reg succeeded");
                    user = firebaseAuth.getCurrentUser();
                    Sys sys = Sys.getInstance();
                    loadUser(context);
                    Log.d(TAG, "onComplete: user loaded");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logout()
    {
        user = null;
        isLoggedIn = false;
        firebaseAuth.signOut();
    }

    public void refreshUser(Context context)
    {
        user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            return;
        }
        isLoggedIn = true;
        loadUser(context);
    }

    public void loadUser(final Context context)
    {
        String email = user.getEmail();
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Sys sys = Sys.getInstance();
                        sys.setDoc(document);
                        sys.loadUser();
                        context.startActivity(new Intent(context, HomeActivity.class));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void uploadUser(User newUser, boolean isNew)
    {
        CollectionReference users = db.collection("users");
        Map<String, Object> userData = new HashMap<>();
        userData.put("first name", newUser.getFirstName());
        userData.put("last name", newUser.getLastName());
        userData.put("sex", newUser.getSex());
        double[] healthData = newUser.getHealthData();
        userData.put("activity", healthData[4]);
        userData.put("weight", healthData[0]);
        userData.put("height", healthData[1]);
        Date birth =  new Date();
        birth.setDate(newUser.getDOBArray()[0]);
        birth.setMonth(newUser.getDOBArray()[1]);
        birth.setYear(newUser.getDOBArray()[2]);
        Log.d(TAG, "uploadUser: " + birth.toString());
        userData.put("dateofbirth", birth);
        //putting the goal in if it is new, otherwise leave goal as it is
        if(isNew) {
            userData.put("mainGoalType", newUser.getGoalType());
            userData.put("targetWeight", newUser.getTargetWeight());
            userData.put("diffPerWeek", newUser.getDiffPerWeek());
        }
        if(isNew)
        {
            users.document(user.getEmail()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    Log.d(TAG, "set the document");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
        else {
            users.document(user.getEmail()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    Log.d(TAG, "updated the document");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
    }

    public void uploadUserGoal(User newUser)
    {
        CollectionReference users = db.collection("users");
        Map<String, Object> goalData = new HashMap<>();
        goalData.put("mainGoalType", newUser.getGoalType());
        goalData.put("diffPerWeek", newUser.getDiffPerWeek());
        goalData.put("targetWeight", newUser.getTargetWeight());
        users.document(user.getEmail()).update(goalData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Goal successfully added");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing goal", e);
                    }
                });
    }


    //add meal functions
    public Ingredient convertToIngredient(QueryDocumentSnapshot q)
    {
        String name = q.get("name").toString();
        String caloriesStr = q.get("calories").toString();
        String weightStr = q.get("weight").toString();
        int calories = Sys.convertToInt(caloriesStr);
        double weight = Sys.convertToDouble(weightStr);
        return new Ingredient(name, weight, calories);
    }

    public void getSearchedIngredients(String searchWord, OnCompleteListener<QuerySnapshot> listener)
    {
        //need two lists, one for things that match by name and one for things that match by keyword
        CollectionReference ing = db.collection("ingredients");
        ing.whereEqualTo("name", searchWord).get().addOnCompleteListener(listener);
        ing.whereArrayContains("keyword", searchWord).get().addOnCompleteListener(listener);
    }

    public void getIngredients( OnCompleteListener<QuerySnapshot> listener)
    {
        ArrayList <Meal> m= new ArrayList<Meal>();

        db.collection("ingredients").get().addOnCompleteListener(listener);
    }
    public void getOwnIngredients(OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference ing = db.collection("users").document(user.getEmail()).collection("ingredients");
        ing.get().addOnCompleteListener(listener);
    }

    public void uploadIngredient(String name, double weight, int calories, OnCompleteListener listener)
    {
        CollectionReference users = db.collection("users");
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("weight", weight);
        data.put("calories", calories);
        users.document(user.getEmail()).collection("ingredients").add(data).addOnCompleteListener(listener);
    }

    //add exercise functions
    public Exercise convertToExercise(QueryDocumentSnapshot q)
    {
        Exercise ex;
        String type = q.get("type").toString();
        String name = q.get("name").toString();
        int calories = Integer.parseInt(q.get("calories").toString());
        int duration = Integer.parseInt(q.get("duration").toString());
        if(type.equals("cardio"))
        {
            int distance = Integer.parseInt(q.get("distance").toString());
            ex = new CardioExercise(name, calories, duration, distance);
        }
        else
        {
            int reps = Integer.parseInt(q.get("reps").toString());
            ex = new StrengthExercise(name, calories, duration, reps);
        }
        return ex;
    }

    public void getSearchedExercises(String searchWord, OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference exerciseCollection = db.collection("exercises");
        exerciseCollection.whereEqualTo("name", searchWord).get().addOnCompleteListener(listener);
    }

    public void getExercises(OnCompleteListener<QuerySnapshot> listener)
    {
        db.collection("exercises").get().addOnCompleteListener(listener);
    }




    public void getOwnExercises(OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference exerciseCollection = db.collection("users").document(user.getEmail()).collection("exercises");
        exerciseCollection.get().addOnCompleteListener(listener);
    }

    public void uploadCardioExercise(String name, int duration, int calories, int distance, OnCompleteListener listener)
    {
        CollectionReference users = db.collection("users");
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "cardio");
        data.put("duration", duration);
        data.put("calories", calories);
        data.put("distance", distance);
        users.document(user.getEmail()).collection("exercises").add(data).addOnCompleteListener(listener);
    }

    public void uploadStrengthExercise(String name, int duration, int calories, int reps, OnCompleteListener listener)
    {
        CollectionReference users = db.collection("users");
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("type", "strength");
        data.put("duration", duration);
        data.put("calories", calories);
        data.put("reps", reps);
        users.document(user.getEmail()).collection("exercises").add(data).addOnCompleteListener(listener);
    }

    //for uploading/downloading meals and exercises
    public void downloadMeals(OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference mealCollection = db.collection("users").document(user.getEmail()).collection("meals");
        mealCollection.get().addOnCompleteListener(listener);
    }

    public void getMealIngredients(String docID, OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference ingCollection = db.collection("users").document(user.getEmail()).collection("meals")
                .document(docID).collection("ingredients");
        ingCollection.get().addOnCompleteListener(listener);
    }

    public void uploadMeal(final Meal m) //for uploading a new meal
    {
        CollectionReference mealCollection = db.collection("users").document(user.getEmail()).collection("meals");
        Map<String, Object> data = new HashMap<>();
        data.put("name", m.getName());
        data.put("type", m.getMealType());
        ArrayList<Integer> timeArray = new ArrayList<>();
        LocalDateTime time = m.getDateTime();
        timeArray.add(time.getYear());
        timeArray.add(time.getMonthValue());
        timeArray.add(time.getDayOfMonth());
        timeArray.add(time.getHour());
        timeArray.add(time.getMinute());
        data.put("time", timeArray);
        mealCollection.document(m.docID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadIngredients(m);
            }
        });
    }

    public void uploadIngredients(final Meal m)
    {
        final CollectionReference ingredientsCollection = db.collection("users").document(user.getEmail())
                .collection("meals").document(m.docID).collection("ingredients");
        Task<QuerySnapshot> task = ingredientsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        document.getReference().delete();
                    }
                    for(int i=0; i<m.getIngredients().size(); i++)
                    {
                        Ingredient temp = m.getIngredients().get(i);
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", temp.getName());
                        data.put("weight", temp.getWeight());
                        data.put("calories", temp.getCalories());
                        ingredientsCollection.add(data);
                    }
                }
            }
        });
    }

    public void downloadWorkouts(OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference workoutCollection = db.collection("users").document(user.getEmail()).collection("workouts");
        workoutCollection.get().addOnCompleteListener(listener);
    }

    public void getWorkoutExercises(String docID, OnCompleteListener<QuerySnapshot> listener)
    {
        CollectionReference exerciseCollection = db.collection("users").document(user.getEmail()).collection("workouts")
                .document(docID).collection("exercises");
        exerciseCollection.get().addOnCompleteListener(listener);
    }

    public void uploadWorkout(final Workout w, LocalDate selectedDate)
    {
        CollectionReference workouts = db.collection("users").document(user.getEmail()).collection("workouts");
        Map<String, Object> data = new HashMap<>();
        data.put("name", w.getName());
        ArrayList<Integer> timeArray = new ArrayList<>();
        LocalDateTime time = w.getDateTime();
        timeArray.add(time.getYear());
        timeArray.add(time.getMonthValue());
        timeArray.add(time.getDayOfMonth());
        timeArray.add(time.getHour());
        timeArray.add(time.getMinute());
        data.put("time", timeArray);
        workouts.document(w.docID).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    uploadExercises(w);
                }
            });
    }

    public void uploadExercises(final Workout w)
    {
        final CollectionReference exercisesCollection = db.collection("users").document(user.getEmail())
                .collection("workouts").document(w.docID).collection("exercises");
        //clear out the collection
        Task<QuerySnapshot> task = exercisesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        document.getReference().delete();
                    }
                    for(int i=0; i<w.getExercises().size(); i++)
                    {
                        Exercise temp = w.getExercises().get(i);
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", temp.getName());
                        data.put("duration", temp.getDuration());
                        data.put("calories", temp.getCalories());
                        if(temp instanceof CardioExercise)
                        {
                            data.put("type", "cardio");
                            data.put("distance", ((CardioExercise) temp).getDistance());
                        }
                        if(temp instanceof StrengthExercise)
                        {
                            data.put("type", "strength");
                            data.put("reps", ((StrengthExercise) temp).getReps());
                        }
                        exercisesCollection.add(data);
                    }
                }
            }
        });

    }
}
