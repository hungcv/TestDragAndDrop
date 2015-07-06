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
    public static final int CELL_SIZE = AppConstant.CELL_SIZE + 2 * ScreenHelper.dpToPx(10);
    public static final int CELL_MAX_Y = CELL_SIZE - ScreenHelper.dpToPx(10);
    public static final int CELL_MIN_Y = ScreenHelper.dpToPx(10);

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

    public int getIndexByCoordinate(float x) {
        int count = list.size();
        for (int i = count - 1; i >= 0; i--) {
            if (x > (i) * CELL_SIZE) {
                return i;
            }
        }
        return 0;
    }

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

