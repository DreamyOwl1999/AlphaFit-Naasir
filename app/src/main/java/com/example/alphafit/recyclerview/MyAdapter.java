package com.example.alphafit.recyclerview;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alphafit.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<ListItem> itemList;
    private Dialog dialog;
    private OnItemClickListener mListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener l)
    {
        mListener = l;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewInfo;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.ingredient_name);
            textViewInfo = itemView.findViewById(R.id.ingredient_info);

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

    public MyAdapter(ArrayList<ListItem> list)
    {
        itemList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.popup);
        dialog.setCancelable(true);
        MyViewHolder mvh = new MyViewHolder(v, mListener);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ListItem currentItem = itemList.get(position);

        holder.textViewName.setText(currentItem.getTextName());
        holder.textViewInfo.setText(currentItem.getTextInfo());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
