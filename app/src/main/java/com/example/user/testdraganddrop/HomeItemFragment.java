package com.example.user.testdraganddrop;

import android.content.ClipData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 7/5/2015.
 */
public class HomeItemFragment extends Fragment {

    private static final String KEY_ITEMS = "items";
    private static final String KEY_CELL_WIDTH = "cell_width";
    private static final String KEY_CELL_HEIGHT = "cell_height";
    private static final String TAG = "HomeItemFragment";

    private List<ItemGrid> itemGridList;
    public List<ItemGrid> draggingItemList;
    private RecyclerView recyclerView;
    public GridAdapter gridAdapter;
    public int draggingPosition = -1;
    public int startDragPosition = -1;
    public boolean hasEmptyCell;

    public static HomeItemFragment newInstance(List<ItemGrid> list, int cellWidth, int cellHeight) {
        HomeItemFragment fragment = new HomeItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ITEMS, (ArrayList<ItemGrid>) list);
        bundle.putInt(KEY_CELL_WIDTH, cellWidth);
        bundle.putInt(KEY_CELL_HEIGHT, cellHeight);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemGridList = getArguments().getParcelableArrayList(KEY_ITEMS);
        checkHasEmptyCell();
        int cellWidth = getArguments().getInt(KEY_CELL_WIDTH);
        int cellHeight = getArguments().getInt(KEY_CELL_HEIGHT);
        View rootView = inflater.inflate(R.layout.fragment_item_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 5, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridAdapter = new GridAdapter(itemGridList, onDragItem, cellWidth, cellHeight);
        recyclerView.setAdapter(gridAdapter);
        return rootView;
    }

    void checkHasEmptyCell() {
        for (ItemGrid item : itemGridList) {
            if (item.id == 0) {
                hasEmptyCell = true;
                return;
            }
        }
        hasEmptyCell = false;
    }

    private OnDragItem onDragItem = new OnDragItem() {
        @Override
        public void onStartDrag(ItemViewHolder holder, int position) {
            ItemGrid itemGrid = itemGridList.get(position);
            if (itemGrid.id > 0) {
                startDragPosition = position;
                draggingPosition = position;
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.image);
                DragObject object = new DragObject(DragObject.From.GRID_VIEW, itemGrid, position);
                holder.image.startDrag(data, shadowBuilder, object, 0);
                holder.itemView.setVisibility(View.INVISIBLE);
                startMoveAnimation();
            }
        }
    };

    public void setMoveAnimation(RecyclerView.ItemAnimator itemAnimator) {
        recyclerView.setItemAnimator(itemAnimator);
    }

    public void startMoveAnimation() {
        draggingItemList = new ArrayList<>(itemGridList);
        final Animation rotate = AnimationHelper.createFastRotateAnimation();
        int size = itemGridList.size();
        for (int i = 0; i < size; i++) {
            if (i != startDragPosition && itemGridList.get(i).id > 0) {
                recyclerView.findViewHolderForAdapterPosition(i).itemView.startAnimation(rotate);
            }
        }
    }

    ItemViewHolder getViewHolderByPosition(int position) {
        return (ItemViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
    }

    public void stopAnimation() {
        int size = itemGridList.size();
        for (int i = 0; i < size; i++) {
            ItemViewHolder holder = getViewHolderByPosition(i);
            if (holder != null) {
                holder.itemView.clearAnimation();
            }
        }
    }

    public void deleteDragItemAndStop() {
        ItemGrid itemGrid = itemGridList.get(startDragPosition);
        int index = draggingItemList.indexOf(itemGrid);
        draggingItemList.remove(index);
        draggingItemList.add(index, new ItemGrid(0, null, 0));
        hasEmptyCell = true;
        stopDragAndDrop(true);
    }

    public void insertItem(int position, ItemGrid item) {
        draggingItemList.remove(position);
        draggingItemList.add(position, item);
        stopDragAndDrop(true);
        checkHasEmptyCell();
    }

    public void swapCell(int oldPosition, int newPosition) {
        Log.e(TAG, "swap(" + oldPosition + "," + newPosition + ")");
        Collections.swap(draggingItemList, oldPosition, newPosition);
        gridAdapter.notifyItemMoved(oldPosition, newPosition);
    }

    public void stopDragAndDrop(boolean changed) {
        if (draggingPosition >= 0) {
            stopAnimation();
            draggingPosition = -1;
            startDragPosition = -1;
            if (changed) {
                itemGridList.clear();
                itemGridList.addAll(draggingItemList);
            }
            gridAdapter.notifyDataSetChanged();
        }
    }

}
