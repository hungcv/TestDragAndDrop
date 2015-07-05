package com.example.user.testdraganddrop;

/**
 * Created by User on 7/4/2015.
 */
public class DragObject {
    public enum From {
        TAB_BAR,
        GRID_VIEW
    }

    /**
     * 1: from tab bar
     * <p/>
     * 2: from grid view
     */
    public From from;

    public ItemGrid itemGrid;

    public int position;

    public DragObject(From from, ItemGrid itemGrid, int position) {
        this.from = from;
        this.itemGrid = itemGrid;
        this.position = position;
    }
}
