package com.example.user.testdraganddrop;

import android.content.ClipData;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
    private List<ItemGrid> itemTabList;
    private List<ItemGrid> draggingList;
    private int startDragTabPosition = -1;
    private int draggingPosition = -1;

    private int gridCellHeight;
    private int gridCellWidth;
    private boolean isPortrait;
    private long previousTimeOnPosition;
    private HomeItemFragment currentFragment;

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
        rvTabBar.getLayoutParams().height = tabBarAdapter.CELL_SIZE;
        rvTabBar.getLayoutParams().width = 4 * tabBarAdapter.CELL_SIZE;
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
            ItemGrid itemGrid = itemTabList.get(position);
            if (itemGrid.id > 0) {
                previousTimeOnPosition = System.currentTimeMillis();
                draggingPosition = position;
                startDragTabPosition = position;
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(holder.image);
                DragObject object = new DragObject(DragObject.From.TAB_BAR, itemGrid, position);
                holder.image.startDrag(data, shadowBuilder, object, 0);
                holder.itemView.setVisibility(View.INVISIBLE);
                startRotateAnimation();
            }
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

    private long previousTimeOnSwipe;

    private View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            DragObject dragObject = (DragObject) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (v.getId() == R.id.rv_tab_bar) {
                        currentFragment = getCurrentFragment();
                        currentFragment.startMoveAnimation();
                        startRotateAnimation();
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
                            handleMoveWithinGrid(dragObject, event);
                        }
                    }

                    if (event.getX() > AppConstant.MAX_X_2_SWIPE) {
                        long now = System.currentTimeMillis();
                        if (previousTimeOnSwipe == 0) {
                            previousTimeOnSwipe = now;
                        }
                        if (now - previousTimeOnSwipe > 1000) {
                            Log.e(TAG, "SWIPE RIGHT");
                            doSwipeToRight();
                            previousTimeOnSwipe = 0;
                            return false;
                        }
                    } else if (event.getX() < AppConstant.MIN_X_2_SWIPE) {
                        long now = System.currentTimeMillis();
                        if (previousTimeOnSwipe == 0) {
                            previousTimeOnSwipe = now;
                        }
                        if (now - previousTimeOnSwipe > 1000) {
                            Log.e(TAG, "SWIPE LEFT");
                            doSwipeToLeft();
                            previousTimeOnSwipe = 0;
                            return false;
                        }
                    } else {
                        previousTimeOnSwipe = 0;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    candidateStopDrag();
                    break;
                case DragEvent.ACTION_DROP:
                    if (v.getId() == R.id.rv_tab_bar) {
                        if (dragObject.from == DragObject.From.TAB_BAR) {
                            stopDragAndDrop(true);
                        } else {
                            dragGrid2Tab(event, dragObject);
                        }
                    } else {
                        if (dragObject.from == DragObject.From.TAB_BAR) {
                            dragTab2Grid(event, dragObject);
                        } else {
                            currentFragment.stopDragAndDrop(true);
                        }
                    }
                    currentFragment.stopAnimation();
                    stopTabAnimation();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    void dragTab2Grid(DragEvent event, DragObject dragObject) {
        // move item from TAB to Grid
        currentFragment.draggingPosition = currentFragment.draggingPosition >= 0 ? currentFragment.draggingPosition : findCellIndexInGrid(event.getX(), event.getY());
        if (currentFragment.draggingItemList.get(currentFragment.draggingPosition).id == 0) {
            currentFragment.insertItem(currentFragment.draggingPosition, dragObject.itemGrid);
            removeDragItemAndStop();
        }
    }

    void dragGrid2Tab(DragEvent event, DragObject dragObject) {
        draggingPosition = draggingPosition >= 0 ? draggingPosition : tabBarAdapter.getIndexByCoordinate(event.getX());
        if (draggingList.get(draggingPosition).id == 0) {
            insertItem2Tab(draggingPosition, dragObject.itemGrid);
            currentFragment.deleteDragItemAndStop();
        }
    }

    void candidateStopDrag() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDragAndDrop(false);
                currentFragment.stopDragAndDrop(false);
            }
        }, 300);
    }

    void doSwipeToRight() {
        if (viewPager.getCurrentItem() < adapter.getCount() - 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    }

    void doSwipeToLeft() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }

    void removeDragItemAndStop() {
        ItemGrid itemGrid = itemTabList.get(startDragTabPosition);
        int index = draggingList.indexOf(itemGrid);
        draggingList.remove(index);
        draggingList.add(index, new ItemGrid(0, null, 0));
        stopDragAndDrop(true);
    }

    void stopTabAnimation() {
        for (int i = 0; i < 4; i++) {
            RecyclerView.ViewHolder holder = rvTabBar.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.itemView.clearAnimation();
            }
        }
    }

    HomeItemFragment getCurrentFragment() {
        return getFragmentByPosition(viewPager.getCurrentItem());
    }

    HomeItemFragment getFragmentByPosition(int position) {
        return (HomeItemFragment) adapter.instantiateItem(viewPager, position);
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
        for (int i = columnCount - 1; i >= 0; i--) {
            if (x > (i) * gridCellWidth) {
                column = i;
                break;
            }
        }
        for (int i = rowCount - 1; i >= 0; i--) {
            if (y > (i) * gridCellHeight) {
                row = i;
                break;
            }
        }
        index = row * columnCount + column;
        Log.d(TAG, "index:" + index + "row:" + row
                + " column:" + column + " x:" + x);
        return index;
    }

    void handleMoveGrid2Tab(DragObject object, DragEvent event) {
        draggingPosition = tabBarAdapter.getIndexByCoordinate(event.getX());
        if (tabBarAdapter.hasEmptyCell()) {
            if (itemTabList.get(draggingPosition).id == 0) {

            } else {
//                TODO: sortItem
            }
        }
    }

    void insertItem2Tab(int position, ItemGrid itemGrid) {
        draggingList.remove(position);
        draggingList.add(position, itemGrid);
        stopDragAndDrop(true);
    }


    void handleMoveWithinGrid(DragObject object, DragEvent event) {
        int oldPosition = currentFragment.draggingPosition;
        currentFragment.draggingPosition = findCellIndexInGrid(event.getX(), event.getY());
        if (validHoldTime(oldPosition, currentFragment.draggingPosition)) {
            currentFragment.swapDraftCell(oldPosition, currentFragment.draggingPosition);
        } else {
            currentFragment.draggingPosition = oldPosition;
        }
    }

    void handleMoveTab2Grid(DragEvent event) {
        if (currentFragment.hasEmptyCell) {
            int oldPosition = currentFragment.draggingPosition;
            currentFragment.draggingPosition = findCellIndexInGrid(event.getX(), event.getY());
            if (validHoldTime(oldPosition, currentFragment.draggingPosition)) {
                currentFragment.holdOnToInsert(oldPosition);
            } else {
                currentFragment.draggingPosition = oldPosition;
            }
        }
    }

    boolean validHoldTime(int oldIndex, int newIndex) {
        if (oldIndex != newIndex) {
            if (previousTimeOnPosition == 0) {
                previousTimeOnPosition = System.currentTimeMillis();
            } else {
                long now = System.currentTimeMillis();
                if (now - previousTimeOnPosition > 500) {
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
        if (event.getY() < tabBarAdapter.CELL_MIN_Y || event.getY() > tabBarAdapter.CELL_MAX_Y) {
            return;
        }
        int oldPosition = draggingPosition;
        draggingPosition = tabBarAdapter.getIndexByCoordinate(event.getX());
        if (validHoldTime(oldPosition, draggingPosition)) {
            Log.e(TAG, "swap(" + oldPosition + "," + draggingPosition + ")");
            if (oldPosition < draggingPosition) {
                rvTabBar.setItemAnimator(new SlideInLeftAnimator());
                if (draggingList.get(startDragTabPosition).id > 0) {
                    for (int i = oldPosition; i < draggingPosition; i++) {
                        Collections.swap(draggingList, i, i + 1);
                        tabBarAdapter.notifyItemMoved(i, i + 1);
                    }
                } else {
                    Collections.swap(draggingList, oldPosition, draggingPosition);
                    tabBarAdapter.notifyItemMoved(oldPosition, draggingPosition);
                }
            } else {
                rvTabBar.setItemAnimator(new SlideInRightAnimator());
                if (draggingList.get(startDragTabPosition).id > 0) {
                    for (int i = oldPosition; i > draggingPosition; i--) {
                        Collections.swap(draggingList, i, i - 1);
                        tabBarAdapter.notifyItemMoved(i, i - 1);
                    }
                } else {
                    Collections.swap(draggingList, oldPosition, draggingPosition);
                    tabBarAdapter.notifyItemMoved(oldPosition, draggingPosition);
                }
            }
        } else {
            draggingPosition = oldPosition;
        }
    }

    public void stopDragAndDrop(boolean changed) {
        if (draggingPosition >= 0) {
            draggingPosition = -1;
            startDragTabPosition = -1;
            stopTabAnimation();
            if (changed) {
                itemTabList.clear();
                itemTabList.addAll(draggingList);
            }
            tabBarAdapter.notifyDataSetChanged();
        }
    }
}
