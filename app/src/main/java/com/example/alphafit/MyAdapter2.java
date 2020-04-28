package com.example.alphafit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder> {
    private int j;
    private ArrayList<CList> galleryList;
    private Context context;

    public MyAdapter2(Context context, ArrayList<CList> galleryList, int j) {
        this.j=j;
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyAdapter2.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Log.d("myTag", i+"");
        if(galleryList.get(i).getFill()){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_filled_block, viewGroup, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_empty_block, viewGroup, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(MyAdapter2.ViewHolder viewHolder, int i) {

        if(galleryList.get(i).getFill()) {
            viewHolder.block.setBlockCol(galleryList.get(i).getColor());
            viewHolder.block.setOnClickListener(galleryList.get(i).getLis());
            viewHolder.block.setId(galleryList.get(i).getId());
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(134, 134);
            viewHolder.block.setLayoutParams(blockParams);
        }
        else{
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(134, 134);
            viewHolder.eBlock.setLayoutParams(blockParams);
        }
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Block block;
        private EmptyBlock eBlock;
        public ViewHolder(View view) {
            super(view);

            block=(Block) view.findViewById(R.id.block);
            eBlock=(EmptyBlock) view.findViewById(R.id.eBlock);
        }
    }
}
