package com.example.alphafit;

import android.util.Log;

import com.example.alphafit.recyclerview.ListHomeItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;


public class Almanac {

    private ArrayList<com.example.alphafit.Task> scheduledTasks;
    private com.example.alphafit.Task temp2;
    private ListHomeItem itemB,itemL,itemD,itemS,itemW, temp;
    private LocalDate selectedDay;
    private ArrayList<ListHomeItem> itemList;
    private int [] order;
    private int hAmB,hAmL,hAmD,n,hAm,c;
    private String time,info;

    public Almanac(){

        Sys sys = Sys.getInstance();

        scheduledTasks = sys.getScheduledTasks(sys.getSelectedDate());
        itemList = new ArrayList();
        //selectedDay = LocalDate.now();
        //meal = sys.getMeal("breakfast");
        if(scheduledTasks.size()!=0) {
            for(int k=0;k<scheduledTasks.size();k++) {
                if(scheduledTasks.get(k) instanceof Meal) {
                    Meal meal = (Meal) scheduledTasks.get(k);
                    itemList.add(sys.convertToListHomeItemFromMeal(meal));
                }
                else if(scheduledTasks.get(k) instanceof Workout){
                    Workout workout = (Workout) scheduledTasks.get(k);
                    itemList.add(sys.convertToListHomeItemFromWorkout(workout));
                }
            }
        }
        else {
            if(sys.getMeal("breakfast").getTotalWeight()!=0){
                itemB = sys.convertToListHomeItemFromMeal(sys.getMeal("breakfast"));
                itemList.add(itemB);
            }
            if(sys.getMeal("lunch").getTotalWeight()!=0) {
                itemL = sys.convertToListHomeItemFromMeal(sys.getMeal("lunch"));
                itemList.add(itemL);
            }
            if(sys.getMeal("dinner").getTotalWeight()!=0){
                itemD = sys.convertToListHomeItemFromMeal(sys.getMeal("dinner"));
                itemList.add(itemD);
            }
            if(sys.getMeal("").getTotalWeight()!=0){
                itemS = sys.convertToListHomeItemFromMeal(sys.getMeal(""));
                itemList.add(itemS);
            }
            if(sys.getNoWorkouts()!=0){
                int noWork = sys.getNoWorkouts();
                for(int h=0;h<noWork;h++){
                    itemW = sys.convertToListHomeItemFromWorkout(sys.getSelectedWorkout(h));
                    itemList.add(itemW);
                }
            }

        }

        order = new int[itemList.size()];
        n=order.length;
        hAm=0;
        order=getOrder();

        Log.d("Testing1","Hello");

        for(int i=0; i < n; i++){
            for(int j=1; j < (n-i); j++){
                if(order[j-1] > order[j]){
                    Collections.swap(itemList, j-1, j);
                }
            }
        }

        Log.d("Testing2","Hello");

        c=scheduledTasks.size();

        if(scheduledTasks.size()!=0) {
            for (int a = 0; a < c; a++) {
                for (int b = 1; b < (c - a); b++) {
                    if (order[b - 1] > order[b]) {
                        Collections.swap(itemList, b-1, b);
                    }
                }
            }
        }
    }

    public int [] getOrder(){

        if(scheduledTasks.size()!=0){
            for(int i=0; i<scheduledTasks.size() ;i++){
                time=itemList.get(i).getTime();
                hAm = Integer.parseInt(time.replaceAll(":", ""));
                order[i]=hAm;
            }
        }
        else {
            for(int j=0; j<itemList.size();j++) {
                time = itemList.get(j).getTime();
                hAm = Integer.parseInt(time.replaceAll(":", ""));
                order[j] = hAm;
            }
        }
        return order;
    }


    public ArrayList<com.example.alphafit.Task> getTaskList() {
        return scheduledTasks;
    }

    public ListHomeItem getItem(int i){
        return itemList.get(i);
    }

    public int getItemListLength(){
        return itemList.size();

    }

}
