package com.example.alphafit.recyclerview;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphafit.R;

import java.util.ArrayList;

/***
 * Class handling the adaptation list items displayed in HomeActivity
 */

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomePageViewHolder> {
    private ArrayList<ListHomeItem> listItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position, int listSize);
    }

    public void setOnItemClickListener(HomePageAdapter.OnItemClickListener l)
    {
        mListener = l;
    }

    public static class HomePageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewTitle;
        public TextView textViewCalories;
        public TextView textViewDescription;
        public TextView textViewTime;

        public HomePageViewHolder(@NonNull View itemView, final HomePageAdapter.OnItemClickListener listener, final ArrayList<ListHomeItem> items) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.homepage_name);
            textViewCalories = itemView.findViewById(R.id.homepage_calories);
            textViewDescription = itemView.findViewById(R.id.homepage_description);
            textViewTime = itemView.findViewById(R.id.homepage_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position, items.size());
                        }
                    }
                }
            });
        }
    }

    public HomePageAdapter(ArrayList<ListHomeItem> items)
    {
        listItems = items;
    }

    @NonNull
    @Override
    public HomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_item, parent, false);
        HomePageViewHolder hpvh = new HomePageViewHolder(v, mListener, listItems);
        return hpvh;

    }

    @Override
    public void onBindViewHolder(@NonNull HomePageViewHolder holder, int position) {
        ListHomeItem currentItem = listItems.get(position);
        holder.textViewTitle.setText(currentItem.getTitle());
        holder.textViewCalories.setText(currentItem.getCalories());
        holder.textViewDescription.setText(currentItem.getDescription());
        if(currentItem.getCalories().equals("0 cal"))
        {
            holder.textViewTime.setText("no time");
        }
        else {
            holder.textViewTime.setText(currentItem.getTime());
        }
        if(currentItem.getCompleted())
        {
            holder.textViewTitle.setTextColor(0xff32a852);
            holder.textViewCalories.setTextColor(0xff32a852);
            holder.textViewDescription.setTextColor(0xff32a852);
            holder.textViewTime.setTextColor(0xff32a852);
        }
        else
        {
            if(position != 0) {
                if (listItems.get(position - 1).getCompleted()) {
                    //then set them to yellow since it is the next item on the list
                    holder.textViewTitle.setTextColor(0xffd4a602);
                    holder.textViewCalories.setTextColor(0xffd4a602);
                    holder.textViewDescription.setTextColor(0xffd4a602);
                    holder.textViewTime.setTextColor(0xffd4a602);
                }
                else
                {
                    //then set them to red since they are further down the list
                    holder.textViewTitle.setTextColor(0xffa83232);
                    holder.textViewCalories.setTextColor(0xffa83232);
                    holder.textViewDescription.setTextColor(0xffa83232);
                    holder.textViewTime.setTextColor(0xffa83232);
                }
            }
            else
            {
                //then set them to yellow since it is the first item on the list
                holder.textViewTitle.setTextColor(0xffd4a602);
                holder.textViewCalories.setTextColor(0xffd4a602);
                holder.textViewDescription.setTextColor(0xffd4a602);
                holder.textViewTime.setTextColor(0xffd4a602);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}