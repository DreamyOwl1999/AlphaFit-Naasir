package com.example.alphafit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.alphafit.android.HomeActivity;
import com.example.alphafit.goals.Goal;
import com.example.alphafit.goals.WeightGoal;
import com.example.alphafit.recyclerview.ListExerciseItem;
import com.example.alphafit.recyclerview.ListGoalItem;
import com.example.alphafit.recyclerview.ListHomeItem;
import com.example.alphafit.recyclerview.ListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import java.util.Date;
import java.util.Map;


/***
 * Class that handles most events in the system
 * Used by different parts of application to communicate with one another safely
 * Implements the singleton pattern
 */

public class Sys implements AlertDialog.OnClickListener {
    private User user;
    private static Sys instance;
    private DocumentSnapshot document;
    private Calendar calendar;
    private HomeActivity homeActivity;
    private com.example.alphafit.Task currentTask;
    private CalendarIterator iterator;

    ArrayList<WeightEntry> weightEntryArrayList;
    ArrayList<Goal> goalArrayList;
    ArrayList<com.example.alphafit.Task> scheduledTasks;
    ArrayList<Goal> goals;

    private static final String TAG = "Sys";


    private Sys() {
        weightEntryArrayList = new ArrayList<>();
        goalArrayList = new ArrayList<>();
        scheduledTasks = new ArrayList<>();
        goals = new ArrayList<>();
        iterator = new DisplayGraphI();
    }

    public static Sys getInstance() {
        if (instance == null) {
            instance = new Sys();
        }
        return instance;
    }

    public Calendar getCalendar()
    {
        return calendar;
    }

    //scheduled task functions
    public void addNewScheduledTask(com.example.alphafit.Task t)
    {
        scheduledTasks.add(t);
    }

    public ArrayList<com.example.alphafit.Task> getScheduledTasks(LocalDate date)
    {
        ArrayList<com.example.alphafit.Task> selectedTasks = new ArrayList<>();
        for(int i=0; i<scheduledTasks.size(); i++)
        {
            if(scheduledTasks.get(i).getDate().isEqual(date))
            {
                selectedTasks.add(scheduledTasks.get(i));
            }
        }
        return selectedTasks;
    }

    public void setCurrentTask(com.example.alphafit.Task t)
        {
        currentTask = t;
    }

    public void moveScheduledToDiary(com.example.alphafit.Task task)
    {
        //remove the task from the scheduling list
        scheduledTasks.remove(task);
        //then add it to the system
        if(task instanceof Meal)
        {
            addNewMeal((Meal) task, ((Meal) task).getMealType(), task.getDate(), false);
            if(homeActivity != null) {
                homeActivity.onResume();
            }
        }
        if(task instanceof Workout)
        {
            addNewWorkout((Workout) task, task.getDate(), false);
            if(homeActivity != null) {
                homeActivity.onResume();
            }
        }
    }

    //goals functions
    public void addGoal(Goal newGoal)
    {
        goals.add(newGoal);
    }

    public void checkGoalCompletion()
    {
        for(int i=0; i<goals.size(); i++)
        {
            goals.get(i).checkCompletion();
        }
    }

    //weight entry functions
    public void addWeightEntry(WeightEntry entry)
    {
        weightEntryArrayList.add(entry);
        if(entry.getDate().isEqual(LocalDate.now())) //if the date set is today
        {
            updateUserWeight(entry.getWeight());
        }
    }

    public void updateUserWeight(double weight)
    {
        user.setWeight(weight);
        DBConnector.getInstance().updateUserWeight(weight);
    }

    public ArrayList<Goal> getGoals()
    {
        return goals;
    }

    public CalendarIterator getIterator()
    {
        return iterator;
    }

    public boolean checkHomePageReady() {
        if (calendar == null) {
            return false;
        }
        return true;
    }

    public void initialiseHomePage() {
        //create calendar
        calendar = new Calendar();
        //here goes iterator = calendar; need it to implement interface first
    }

    public LocalDate getSelectedDate()
    {
        return calendar.getSelectedDay();
    }

    public int[] getDisplayInfo() {
        return calendar.getSelectedInfo();
    }

    public String getNiceDate()
    {
        return calendar.getNiceDate();
    }

    public void changeDay(boolean forward)
    {
        calendar.moveDayForwardOrBackward(forward);
    }

    public boolean changeDaySpecific(int day, int month, int year)
    {
        return calendar.moveToSpecificDay(day, month, year);
    }


    public void setUser(boolean isNew, String fName, String lName, String gender, String w, String h, String activityLevel, int day, int month, int year) {
        int actLevel = 0;
        double weight = Double.parseDouble(w);
        double height = Double.parseDouble(h);
        try {
            actLevel = Integer.parseInt(activityLevel);
        } catch (Exception e) {
            if (activityLevel.equals("Sedentary")) {
                actLevel = 0;
            } else if (activityLevel.equals("Fairly Active")) {
                actLevel = 1;
            } else if (activityLevel.equals("Moderately Active")) {
                actLevel = 2;
            } else if (activityLevel.equals("Very Active")) {
                actLevel = 3;
            }
        }


        String goalType = "";
        double targetWeight = 0, diffPerWeek = 0;
        if(!isNew)
        {
            //save the goal from the old user
            targetWeight = user.getTargetWeight();
            diffPerWeek = user.getDiffPerWeek();
            goalType = user.getGoalType();
        }
        user = new User(fName, lName, gender, weight, height, actLevel, day, month, year);
        if(!isNew) {
            user.setMainGoalFromDatabase(goalType, targetWeight, diffPerWeek);
        }
        DBConnector connector = DBConnector.getInstance();
        connector.uploadUser(user, isNew);
        //test out some things in the log
        Log.d(TAG, "setUser: " + user.getFullName());
        Log.d(TAG, "setUser: " + user.getDOB());
    }

    public void setUserFromDatabase(String fName, String lName, String gender, String w, String h, String activityLevel, int day, int month, int year)
    {
        int actLevel = 0;
        if (user != null) {
            //something has gone wrong
            return;
        }
        double weight = Double.parseDouble(w);
        double height = Double.parseDouble(h);
        try {
            actLevel = Integer.parseInt(activityLevel);
        } catch (Exception e) {
            if (activityLevel.equals("Sedentary")) {
                actLevel = 0;
            } else if (activityLevel.equals("Fairly Active")) {
                actLevel = 1;
            } else if (activityLevel.equals("Moderately Active")) {
                actLevel = 2;
            } else if (activityLevel.equals("Very Active")) {
                actLevel = 3;
            }
        }
        user = new User(fName, lName, gender, weight, height, actLevel, day, month, year);
    }

    public void setUserGoal(int weeks, double target, String type)
    {
        user.setMainGoal(weeks, target, type);
        DBConnector connector = DBConnector.getInstance();
        connector.uploadUserGoal(user);
    }

    public void setUserGoalFromDatabase(String type, double target, double diffPerWeek)
    {
        user.setMainGoalFromDatabase(type, target, diffPerWeek);
    }

    public double getUserBMR()
    {
        double[] data = user.getHealthData();
        return data[3];
    }

    public double[] getUserHealthData()
    {
        return user.getHealthData();
    }

    public String getName()
    {
        return user.getFullName();
    }

    public int getIntake()
    {
        return user.calculateRecIntake();
    }

    public void setDoc(DocumentSnapshot d)
    {
        document = d;
    }

    public DocumentSnapshot getDoc()
    {
        return document;
    }


    public void loadUser()
    {
        if(document != null)
        {
            Map<String, Object> allInfo = document.getData();
            String fName = allInfo.get("first name").toString();
            String lName = allInfo.get("last name").toString();
            String sex = allInfo.get("sex").toString();
            String weight = allInfo.get("weight").toString();
            String height = allInfo.get("height").toString();
            String activity = allInfo.get("activity").toString();
            String goalType = allInfo.get("mainGoalType").toString();
            double target = convertToDouble(allInfo.get("targetWeight").toString());
            double diff = convertToDouble(allInfo.get("diffPerWeek").toString());
            Timestamp dateofbirth = (Timestamp) allInfo.get("dateofbirth");
            Date date = dateofbirth.toDate();
            int day = date.getDay();
            int month = date.getMonth();
            int year = date.getYear();
            Log.d(TAG, "loadUser: the year is " + Integer.toString(year));
            setUserFromDatabase(fName, lName, sex, weight, height, activity, day, month, year);
            setUserGoalFromDatabase(goalType, target, diff);
        }
        else
        {
            Log.d(TAG, "loadUser: Error on getting document");
        }
    }

    public String[] getUserInfo()
    {
        String[] data = new String[3];
        data[0] = user.getFirstName();
        data[1] = user.getLastName();
        data[2] = user.getSex();
        return data;
    }

    public double[] getUserGoalInfo()
    {
        double target = user.getTargetWeight();
        double diffPerWeek = user.getDiffPerWeek();
        int weeks = (int) Math.abs(((target-user.getWeight())/diffPerWeek));
        return new double[]{user.getTargetWeight(), weeks};
    }

    public String getUserGoalType()
    {
        return user.getGoalType();
    }

    public int[] getUserBirthArray()
    {
        return user.getDOBArray();
    }

    public void setHomeActivity(HomeActivity h)
    {
        homeActivity = h;
    }

    public void loadDatabaseMeals()
    {
        DBConnector connector = DBConnector.getInstance();
        connector.downloadMeals(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                onCompleteMeal(task);
            }
        });
    }

    public void loadDatabaseWorkouts()
    {
        DBConnector connector = DBConnector.getInstance();
        connector.downloadWorkouts(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                onCompleteWorkout(task);
            }
        });
    }

    public void onCompleteMeal(@NonNull Task<QuerySnapshot> task)
    {
        if(task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                convertDocumentToMeal(document);
            }
        }
    }

    public void onCompleteWorkout(@NonNull Task<QuerySnapshot> task)
    {
        if(task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                convertDocumentToWorkout(document);
            }
        }
    }

    public void convertDocumentToMeal(QueryDocumentSnapshot q)
    {
        String id = q.getId();
        String name = q.get("name").toString();
        String type = q.get("type").toString();
        ArrayList<Long> timeData = (ArrayList<Long>) q.get("time");
        int year = timeData.get(0).intValue();
        int month = timeData.get(1).intValue();
        int day = timeData.get(2).intValue();
        int hours = timeData.get(3).intValue();
        int minutes = timeData.get(4).intValue();
        LocalDate date = LocalDate.of(year, month, day);
        Log.d(TAG, "convertDocumentToMeal: the added date is " + date.toString());
        LocalTime time = LocalTime.of(hours, minutes);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Meal m = new Meal(name, type, 0, 0, 0);
        m.setTime(dateTime);
        m.setDocID(id);
        //need to add ingredients
        loadUpIngredients(m, q.getId());
    }

    public void loadUpIngredients(final Meal m, String docID)
    {
        DBConnector connector = DBConnector.getInstance();
        connector.getMealIngredients(docID, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                onCompleteIngredient(task, m);
            }
        });
    }

    public void onCompleteIngredient(Task<QuerySnapshot> task, Meal m)
    {
        if(task.isSuccessful())
        {
            DBConnector connector = DBConnector.getInstance();
            for(QueryDocumentSnapshot document : task.getResult())
            {
                Ingredient ing = connector.convertToIngredient(document);
                m.addIngredient(ing);
            }
        }
        //recalculate stats
        m.getTotalCalories();
        m.getTotalWeight();
        //then add the meal to the system
        LocalDate date = m.getDate();
        m.updateCompleted(true);
        addNewMeal(m, m.getMealType(), date, false);
        homeActivity.onResume();
    }

    public void convertDocumentToWorkout(QueryDocumentSnapshot q)
    {
        String id = q.getId();
        String name = q.get("name").toString();
        ArrayList<Long> timeData = (ArrayList<Long>) q.get("time");
        int year = timeData.get(0).intValue();
        int month = timeData.get(1).intValue();
        int day = timeData.get(2).intValue();
        int hours = timeData.get(3).intValue();
        int minutes = timeData.get(4).intValue();
        LocalDate date = LocalDate.of(year, month, day);
        Log.d(TAG, "convertDocumentToMeal: the added date is " + date.toString());
        LocalTime time = LocalTime.of(hours, minutes);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        Workout w = new Workout(name);
        w.setTime(dateTime);
        w.setDocID(id);
        loadUpExercises(w);
    }

    public void loadUpExercises(final Workout w)
    {
        DBConnector connector = DBConnector.getInstance();
        connector.getWorkoutExercises(w.docID, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                onCompleteExercise(task, w);
            }
        });
    }

    public void onCompleteExercise(Task<QuerySnapshot> task, Workout w)
    {
        if(task.isSuccessful())
        {
            DBConnector connector = DBConnector.getInstance();
            for(QueryDocumentSnapshot document : task.getResult())
            {
                w.addExercise(connector.convertToExercise(document));
            }
        }
        //recalculate the stats
        w.getTotalCalories();
        w.getTotalDuration();
        //add workout to the system
        w.updateCompleted(true);
        addNewWorkout(w, w.getDate(), false);
        homeActivity.onResume();
    }

    public void addNewMeal(Meal m, String type, LocalDate date, boolean b)
    {
        if(b) {
            addNewScheduledTask(m);
        }
        else
        {
            calendar.addNewMeal(m, type, date);
        }
    }

    public Meal getMeal(String click)
    {
        return calendar.getSelectedMeal(click);
    }

    public int getNoWorkouts()
    {
        return calendar.getNoWorkouts();
    }

    public Workout getSelectedWorkout(int position)
    {
        return calendar.getSelectedWorkout(position);
    }

    public void addNewWorkout(Workout workout, LocalDate date, boolean b)
    {
        if(b)
        {
            addNewScheduledTask(workout);
        }
        else {
            calendar.addWorkout(workout, date);
        }
    }

    public void replaceWorkout(Workout workout, int pos)
    {
        calendar.replaceWorkout(pos, workout);
    }

    //helper functions
    public static boolean checkForNumber(String s)
    {
        for(int i=0; i<s.length(); i++)
        {
            if(Character.isDigit(s.charAt(i)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkAge()
    {
        if(user.getAge() < User.MIN_AGE)
        {
            return false;
        }
        return true;
    }


    public static double convertToDouble(String s) {
        try {
            double d = Double.parseDouble(s);
            return d;
        } catch (Exception e) {
            Log.d(TAG, "convertToDouble: This wasn't a double." + s);
            //put some kind of exit statement here
            return -1;
        }
    }

    public static int convertToInt(String s)
    {
        try
        {

            int i = (int) convertToDouble(s);
            return i;
        } catch(Exception e)
        {
            Log.d(TAG, "convertToInt: This wasn't an integer." + s);
            //put some kind of exit statement here
            return -1;
        }
    }

    public static ListItem convertToListItem(Ingredient ing)
    {
        ListItem l = new ListItem(ing.getName(), ing.getCalories(), ing.getWeight());
        return l;
    }

    public static ListItem convertToListItem(Meal ing)
    {
        ListItem l = new ListItem(ing.getName(), ing.getTotalCalories(), ing.getTotalWeight());
        return l;
    }

    public static ListExerciseItem convertToListExerciseItem(Exercise e)
    {
        String type = "Strength";
        double repsOrDistance = 0;
        if(e instanceof CardioExercise)
        {
            type = "Cardio";
            repsOrDistance = ((CardioExercise) e).getDistance();
        }
        else
        {
            repsOrDistance = ((StrengthExercise) e).getReps();
        }
        return new ListExerciseItem(e.getName(), e.getCalories(), e.getDuration(), e.getExtraInfo(), type, repsOrDistance);
    }

    public static ListHomeItem convertToListHomeItemFromMeal(Meal m)
    {
        ListHomeItem item;
        String title = m.getName();
        String time = m.getTime().toString();
        int totalCal = m.getTotalCalories();
        String desc = m.getTotalWeight() + " grams";
        desc = makeDescriptionIngredients(desc, m.getIngredients());
        item = new ListHomeItem(title, totalCal, desc, m.getCompleted(), time);
        return item;
    }

    public static ListHomeItem convertToListHomeItemFromWorkout(Workout w)
    {
        ListHomeItem item;
        String title = w.getName();
        String time = w.getTime().toString();
        int totalCal = w.getTotalCalories();
        String desc = w.getTotalDuration() + " minutes";
        desc = makeDescriptionExercises(desc, w.getExercises());
        item = new ListHomeItem(title, totalCal, desc, w.getCompleted(), time);
        return item;
    }

    public static ListGoalItem convertToGoalItemFromGoal(Goal goal)
    {
        ListGoalItem item;
        item = new ListGoalItem(goal.getName(), goal.getProgression(), goal.getAchieved(), goal.getStartDate(), goal.getEndDate());
        return item;
    }

    //for use with scheduled task descriptions
    public static String makeTaskDescription(String currentString, com.example.alphafit.Task task)
    {
        String desc;
        if(task instanceof Meal)
        {
            return makeDescriptionIngredients(currentString, ((Meal) task).getIngredients());
        }
        if(task instanceof Workout)
        {
            return makeDescriptionExercises(currentString, ((Workout) task).getExercises());
        }
        return currentString;
    }

    public static String makeDescriptionIngredients(String currentString, ArrayList<Ingredient> ingredients)
    {
        int maxLength = ListHomeItem.MAX_CHARACTERS;
        int currentLength = currentString.length();
        currentString += " ";
        int pos = 0;
        while((currentLength < maxLength) && pos < ingredients.size())
        {
            currentString += ", " + ingredients.get(pos).getName();
            pos++;
            currentLength = currentString.length();
        }
        return currentString;
    }

    public static String makeDescriptionExercises(String currentString, ArrayList<Exercise> exercises)
    {
        int maxLength = ListHomeItem.MAX_CHARACTERS;
        int currentLength = currentString.length();
        currentString += " ";
        int pos = 0;
        while((currentLength < maxLength) && pos < exercises.size())
        {
            currentString += ", " + exercises.get(pos).getName();
            pos++;
            currentLength = currentString.length();
        }
        return currentString;
    }

    public com.example.alphafit.Task getTask() {
        return currentTask;
    }

    public ArrayList<com.example.alphafit.Task> getTaskList() {
        return scheduledTasks;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == AlertDialog.BUTTON_POSITIVE)
        {
            //they clicked yes so move it
            moveScheduledToDiary(currentTask);
            dialog.dismiss();
        }
        if(which == AlertDialog.BUTTON_NEGATIVE)
        {
            //launch the new activity as normal
            if(currentTask instanceof Meal) {
                homeActivity.launch("meal", ((Meal) currentTask).getMealType());
            }
            else
            {
                homeActivity.launch("workout", "");
            }
        }
    }
}