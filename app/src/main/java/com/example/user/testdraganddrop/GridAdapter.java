package com.example.user.testdraganddrop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by User on 7/4/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private final String TAG = "GridAdapter";

    private List<ItemGrid> list;

    private int cellHeight;
    private int cellWidth;

    @NonNull
    private final OnDragItem onDragItem;

    public GridAdapter(List<ItemGrid> list, @NonNull OnDragItem onDragItem, int cellWidth, int cellHeight) {
        this.list = list;
        this.onDragItem = onDragItem;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gridview, viewGroup, false);
        return new ItemViewHolder(itemView, cellWidth, cellHeight, onDragItem);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, int i) {
        viewHolder.build(i, list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}