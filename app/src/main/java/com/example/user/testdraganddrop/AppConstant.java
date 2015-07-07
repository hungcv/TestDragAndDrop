package com.example.user.testdraganddrop;

/**
 * Created by User on 7/4/2015.
 */
public class AppConstant {

    public static final int CELL_SIZE = (int) (180 * (float) 1080 / 1536);
    public static final int MIN_X_2_SWIPE = CELL_SIZE / 4;
    public static final int MAX_X_2_SWIPE = ScreenHelper.getScreenWidthInPx() - CELL_SIZE / 4;
}
