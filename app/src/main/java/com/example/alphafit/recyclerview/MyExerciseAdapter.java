package com.example.alphafit.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphafit.R;

import java.util.ArrayList;

public class MyExerciseAdapter extends RecyclerView.Adapter<MyExerciseAdapter.MyExerciseViewHolder>{
    ArrayList<ListExerciseItem> listItems;
    private MyExerciseAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(MyExerciseAdapter.OnItemClickListener l)
    {
        mListener = l;
    }

    public static class MyExerciseViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewName;
        public TextView textViewInfo;
        public TextView textViewType;

        public MyExerciseViewHolder(@NonNull View itemView, final MyExerciseAdapter.OnItemClickListener listener)
        {
            super(itemView);
            textViewName = itemView.findViewById(R.id.exercise_name);
            textViewInfo = itemView.findViewById(R.id.exercise_info);
            textViewType = itemView.findViewById(R.id.exercise_type);

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

    public MyExerciseAdapter(ArrayList<ListExerciseItem> items)
    {
        listItems = items;
    }

    @NonNull
    @Override
    public MyExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_exercise_item, parent, false);
        MyExerciseViewHolder mevh = new MyExerciseViewHolder(v, mListener);
        return mevh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyExerciseViewHolder holder, int position)
    {
        ListExerciseItem currentItem = listItems.get(position);
        holder.textViewName.setText(currentItem.getName());
        holder.textViewInfo.setText(currentItem.getInfo());
        holder.textViewType.setText(currentItem.getType());
    }

    @Override
    public int getItemCount()
    {
        return listItems.size();
    }
}