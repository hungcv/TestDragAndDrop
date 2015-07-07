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

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

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
        Log.e(TAG, "hasEmptyCell: " + hasEmptyCell);
        int cellWidth = getArguments().getInt(KEY_CELL_WIDTH);
        int cellHeight = getArguments().getInt(KEY_CELL_HEIGHT);
        View rootView = inflater.inflate(R.layout.fragment_item_layout, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), 6, LinearLayoutManager.VERTICAL, false);
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

        int size = draggingItemList.size();
        boolean hasAnimation = false;
        for (int i = startDragPosition; i < size - 1; i++) {
            recyclerView.setItemAnimator(new SlideInRightAnimator());
            if (draggingItemList.get(i + 1).id == 0) {
                break;
            }
            swapAndAnimation(i, i + 1);
            hasAnimation = true;
        }
        hasEmptyCell = true;
        if (hasAnimation) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopDragAndDrop(true);
                }
            }, 300);
        } else {
            stopDragAndDrop(true);
        }
    }

    public void insertItem(int position, ItemGrid item) {
        draggingItemList.remove(position);
        draggingItemList.add(position, item);
        stopDragAndDrop(true);
        checkHasEmptyCell();
    }

    public void holdOnToInsert(int oldPosition) {
        ItemGrid itemGrid = draggingItemList.get(draggingPosition);
        Log.e(TAG, "" + itemGrid.toString());
        int size = draggingItemList.size();
        if (oldPosition >= 0 && draggingItemList.get(oldPosition).id == 0) {
            if (oldPosition > draggingPosition) {
                for (int i = oldPosition; i > draggingPosition; i--) {
                    swapAndAnimation(i, i - 1);
                }
            } else {
                for (int i = oldPosition; i < draggingPosition; i++) {
                    swapAndAnimation(i, i + 1);
                }
            }
        } else {
            if (itemGrid.id > 0) {
                for (int i = size - 1; i > draggingPosition; i--) {
                    swapAndAnimation(i, i - 1);
                }
            }
        }
    }


    public void swapDraftCell(int oldPosition, int newPosition) {
        Log.e(TAG, "swap(" + oldPosition + "," + newPosition + ")");
        ItemGrid holdOnItem = draggingItemList.get(newPosition);
        if (oldPosition < newPosition) {
            recyclerView.setItemAnimator(new SlideInLeftAnimator());
            for (int i = oldPosition; i < newPosition; i++) {
                swapAndAnimation(i, i + 1);
            }
            if (holdOnItem.id == 0) {
                for (int i = oldPosition; i < newPosition; i++) {
                    if (draggingItemList.get(i).id == 0) {
                        swapAndAnimation(i, newPosition);
                        break;
                    }
                }
            }
        } else {
            recyclerView.setItemAnimator(new SlideInRightAnimator());
            if (holdOnItem.id > 0) {/*Case switch to left item (replace)*/
                for (int i = oldPosition; i > newPosition; i--) {
                    swapAndAnimation(i, i - 1);
                }
            } else {
                swapAndAnimation(oldPosition, newPosition);
            }
        }
    }

    private void swapAndAnimation(int index1, int index2) {
        Log.e(TAG, "swap(" + index1 + "," + index2 + ")");
        Collections.swap(draggingItemList, index1, index2);
        gridAdapter.notifyItemMoved(index1, index2);
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
