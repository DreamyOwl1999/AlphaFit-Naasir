package com.example.alphafit.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphafit.R;

import java.util.ArrayList;

public class MyGoalAdapter extends RecyclerView.Adapter<MyGoalAdapter.MyViewHolder>
{

    private OnItemClickListener mListener;
    private ArrayList<ListGoalItem> itemList;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MyGoalAdapter.OnItemClickListener l)
    {
        mListener = l;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewAchieved;
        public TextView textViewDate;

        public MyViewHolder(@NonNull View itemView, final MyGoalAdapter.OnItemClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.goal_name);
            textViewDescription = itemView.findViewById(R.id.goal_description);
            textViewAchieved = itemView.findViewById(R.id.goal_achieved);
            textViewDate = itemView.findViewById(R.id.date_show);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public MyGoalAdapter(ArrayList<ListGoalItem> list)
    {
        itemList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_goal_item, parent, false);
        //set the dialog stuff in here
        MyViewHolder mvh = new MyViewHolder(v, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListGoalItem currentItem = itemList.get(position);
        holder.textViewName.setText(currentItem.getName());
        holder.textViewDescription.setText(currentItem.getDescription());
        if(currentItem.getCompleted()) {
            holder.textViewAchieved.setText("ACHIEVED");
        }
        else
        {
            holder.textViewAchieved.setText("IN PROGRESS");
        }
        holder.textViewDate.setText(currentItem.getDateShow());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
