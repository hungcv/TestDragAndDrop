package com.example.user.testdraganddrop;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by User on 7/4/2015.
 */
public class TabBarAdapter extends RecyclerView.Adapter<ItemViewHolder> {

//    private final static String TAG = "TabBarAdapter";

    private List<ItemGrid> list;

    @NonNull
    private OnDragItem onDragItem;

    public TabBarAdapter(List<ItemGrid> list, @NonNull OnDragItem onDragItem) {
        this.list = list;
        this.onDragItem = onDragItem;
    }


    public boolean hasEmptyCell() {
        int count = 0;
        for (ItemGrid itemGrid : list) {
            if (itemGrid.id > 0) {
                count++;
            }
        }
        return count < list.size();
    }

//    public void swapIndex(int oldIndex, int newIndex) {
//        if (oldIndex < newIndex) {
//            for (int i = oldIndex; i < newIndex; i++) {
//                Collections.swap(list, i, i + 1);
//                translateToLeftIds.add(String.valueOf(i));
//            }
//        } else {
//            for (int i = oldIndex; i > newIndex; i--) {
//                Collections.swap(list, i, i - 1);
//                translateToRightIds.add(String.valueOf(i));
//            }
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tabbar, viewGroup, false), onDragItem);
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

