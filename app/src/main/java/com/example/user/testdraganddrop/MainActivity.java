package com.example.user.testdraganddrop;

import android.content.ClipData;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private HomeViewPagerAdapter adapter;
    private RecyclerView rvTabBar;
    private LinearLayoutManager tabLayoutManager;
    private View footer;
    private TabBarAdapter tabBarAdapter;
    private int itemTabSize = AppConstant.CELL_SIZE + 2 * ScreenHelper.dpToPx(10);
    private List<ItemGrid> itemTabList;
    private List<ItemGrid> draggingList;
    private int startDragTabPosition = -1;
    private int draggingPosition = -1;

    private int gridCellHeight;
    private int gridCellWidth;
    private boolean isPortrait;

    HomeItemFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        footer = findViewById(R.id.footer);
        initBottomBar();
        getViewPagerSize();
    }

    void getViewPagerSize() {
        viewPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                int size;
                isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
                if (isPortrait) {
                    gridCellWidth = ScreenHelper.getScreenWidthInPx() / 5;
                    gridCellHeight = viewPager.getHeight() / 4;
                    size = 5 * 3;
                } else {
                    gridCellWidth = ScreenHelper.getScreenWidthInPx() / 6;
                    gridCellHeight = viewPager.getHeight() / 2;
                    size = 6 * 2;
                }
                Log.e(TAG, "gridCellWidth:" + gridCellWidth + " gridCellHeight:" + gridCellHeight);
                adapter = new HomeViewPagerAdapter(getSupportFragmentManager(), gridCellWidth, gridCellHeight);
                viewPager.setAdapter(adapter);
                viewPager.setOnDragListener(onDragListener);
                return true;
            }
        });
    }

    void initBottomBar() {
        rvTabBar = (RecyclerView) findViewById(R.id.rv_tab_bar);
        rvTabBar.getLayoutParams().height = itemTabSize;
        rvTabBar.getLayoutParams().width = 4 * itemTabSize;
        rvTabBar.invalidate();
        rvTabBar.setHasFixedSize(true);
        itemTabList = new ArrayList<>();
        itemTabList.add(new ItemGrid(1, "Item 1", R.drawable.ic_temp));
        itemTabList.add(new ItemGrid(2, "Item 2", R.drawable.ic_temp2));
        itemTabList.add(new ItemGrid(3, "Item 3", R.drawable.ic_temp3));
        itemTabList.add(new ItemGrid(0, "Item 4", R.drawable.ic_temp4));
        tabLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTabBar.setLayoutManager(tabLayoutManager);
        tabBarAdapter = new TabBarAdapter(itemTabList, onDragItemTabBar);
        rvTabBar.setAdapter(tabBarAdapter);
        rvTabBar.setOnDragListener(onDragListener);
    }

    private OnDragItem onDragItemTabBar = new OnDragItem() {
        @Override
        public void onStartDrag(ItemViewHolder holder, int position) {
            previousTimeOnPosition = System.currentTimeMillis();
            draggingPosition = position;
            startDragTabPosition = position;
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.image);
            DragObject object = new DragObject(DragObject.From.TAB_BAR, itemTabList.get(position), position);
            holder.image.startDrag(data, shadowBuilder, object, 0);
            holder.itemView.setVisibility(View.INVISIBLE);
            startRotateAnimation();
        }
    };

    void startRotateAnimation() {
        draggingList = new ArrayList<>(itemTabList);
        final Animation rotate = AnimationHelper.createFastRotateAnimation();
        for (int i = 0; i < 4; i++) {
            if (i != startDragTabPosition) {
                getViewHolderByPosition(i).itemView.startAnimation(rotate);
            }
        }
    }

    private ItemViewHolder getViewHolderByPosition(int position) {
        return (ItemViewHolder) rvTabBar.findViewHolderForAdapterPosition(position);
    }

    private long previousTimeOnPosition;
    private int tabMinY = ScreenHelper.dpToPx(10);
    private int tabMaxY = itemTabSize - ScreenHelper.dpToPx(10);

    private View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            DragObject dragObject = (DragObject) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (v.getId() == R.id.rv_tab_bar) {
                        startRotateAnimation();
                        currentFragment = getCurrentFragment();
                        currentFragment.startMoveAnimation();
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    if (dragObject.from == DragObject.From.TAB_BAR) {
                        if (v.getId() == R.id.rv_tab_bar) {
                            handleMoveWithinTab(event);
                        } else {
                            handleMoveTab2Grid(event);
                        }
                    } else {
                        if (v.getId() == R.id.rv_tab_bar) {
                            handleMoveGrid2Tab(dragObject, event);
                        } else {

                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    tabBarAdapter.notifyDataSetChanged();
                    currentFragment.gridAdapter.notifyDataSetChanged();
                    break;
                case DragEvent.ACTION_DROP:
                    Log.e(TAG, "ACTION_DROP X:" + event.getX());
                    currentFragment.stopAnimation();
                    stopTabAnimation();
                    if (v.getId() == R.id.rv_tab_bar) {
                        if (dragObject.from == DragObject.From.TAB_BAR) {
                            itemTabList.clear();
                            itemTabList.addAll(draggingList);
                            tabBarAdapter.notifyDataSetChanged();
                        } else {
                            if (draggingList.get(draggingPosition).id == 0) {
                                //do add new item to TabBar and remove this item from Grid
                                insertItem2Tab(draggingPosition, dragObject.itemGrid);
                                currentFragment.deleteDragItem();
                            }
                        }
                    } else {
                        if (dragObject.from == DragObject.From.TAB_BAR) {
                            currentFragment.insertItem(currentFragment.draggingPosition, dragObject.itemGrid);
                            deleteDragItem();
                        } else {
                        }

                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    void deleteDragItem() {
        itemTabList.remove(startDragTabPosition);
        itemTabList.add(startDragTabPosition, new ItemGrid(0, null, 0));
        tabBarAdapter.notifyDataSetChanged();
    }

    void stopTabAnimation() {
        for (int i = 0; i < 4; i++) {
            rvTabBar.findViewHolderForAdapterPosition(i).itemView.clearAnimation();
        }
    }

    void startMoveAndRotateAnimation(int position, boolean toRight) {
        RecyclerView.ViewHolder holder = rvTabBar.findViewHolderForAdapterPosition(position);
        holder.itemView.clearAnimation();
        holder.itemView.startAnimation(AnimationHelper.createSlideAndRotate(toRight));
    }

    HomeItemFragment getCurrentFragment() {
        return getFragmentByPosition(viewPager.getCurrentItem());
    }

    HomeItemFragment getFragmentByPosition(int position) {
        return (HomeItemFragment) adapter.instantiateItem(viewPager, position);
    }


    void handleMoveTab2Grid(DragEvent event) {
        if (currentFragment.hasEmptyCell) {
            currentFragment.draggingPosition = findCellIndexInGrid(event.getX(), event.getY());
            currentFragment.holdOnPosition();
        }
    }

    int findCellIndexInGrid(float x, float y) {
        int index;
        int column = 0;
        int row = 0;
        int rowCount;
        int columnCount;
        if (isPortrait) {
            rowCount = 4;
            columnCount = 5;
        } else {
            rowCount = 2;
            columnCount = 6;
        }
        for (int i = columnCount; i > 0; i--) {
            if (x > (i - 1) * gridCellWidth) {
                column = i - 1;
                break;
            }
        }
        for (int i = rowCount; i > 0; i--) {
            if (y > (i - 1) * gridCellHeight) {
                row = i - 1;
                break;
            }
        }
        index = row * columnCount + column;
        Log.d(TAG, "index:" + index + "row:" + row
                + " column:" + column + " x:" + x);
        return index;
    }

    void handleMoveGrid2Tab(DragObject object, DragEvent event) {
        if (tabBarAdapter.hasEmptyCell()) {
            if (event.getX() > 3 * itemTabSize) {
                draggingPosition = 3;
            } else if (event.getX() > 2 * itemTabSize) {
                draggingPosition = 2;
            } else if (event.getX() > itemTabSize) {
                draggingPosition = 1;
            } else if (event.getX() > 0) {
                draggingPosition = 0;
            }
            if (itemTabList.get(draggingPosition).id == 0) {

            } else {
//                TODO: sortItem
            }
        }
    }

    void insertItem2Tab(int position, ItemGrid itemGrid) {
        itemTabList.remove(position);
        itemTabList.add(position, itemGrid);
        tabBarAdapter.notifyDataSetChanged();
    }


    void handleMoveWithinGrid(DragObject object, DragEvent event) {

    }

    boolean validHoldTime(int oldIndex, int newIndex) {
        if (oldIndex != newIndex) {
            if (previousTimeOnPosition == 0) {
                previousTimeOnPosition = System.currentTimeMillis();
            } else {
                long now = System.currentTimeMillis();
                if (now - previousTimeOnPosition > 300) {
                    previousTimeOnPosition = now;
                    return true;
                }
            }
        } else {
            previousTimeOnPosition = System.currentTimeMillis();
        }
        return false;
    }

    void handleMoveWithinTab(DragEvent event) {
        if (event.getY() < tabMinY || event.getY() > tabMaxY) {
            return;
        }
        int oldPosition = draggingPosition;
        if (event.getX() > 3 * itemTabSize) {
            draggingPosition = 3;
        } else if (event.getX() > 2 * itemTabSize) {
            draggingPosition = 2;
        } else if (event.getX() > itemTabSize) {
            draggingPosition = 1;
        } else if (event.getX() > 0) {
            draggingPosition = 0;
        }
        if (validHoldTime(oldPosition, draggingPosition)) {
            getViewHolderByPosition(oldPosition).itemView.setVisibility(View.INVISIBLE);
            Log.e(TAG, "swap(" + oldPosition + "," + draggingPosition + ")");
            if (oldPosition < draggingPosition) {
                rvTabBar.setItemAnimator(new SlideInLeftAnimator());
                for (int i = oldPosition; i < draggingPosition; i++) {
                    Collections.swap(draggingList, i, i + 1);
                    tabBarAdapter.notifyItemMoved(i, i + 1);
                }
            } else {
                rvTabBar.setItemAnimator(new SlideInRightAnimator());
                for (int i = oldPosition; i > draggingPosition; i--) {
                    Collections.swap(draggingList, i, i - 1);
                    tabBarAdapter.notifyItemMoved(i, i - 1);
                }
            }
        } else {
            draggingPosition = oldPosition;
        }
    }
}
