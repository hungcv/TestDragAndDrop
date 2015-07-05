package com.example.user.testdraganddrop;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class ScreenHelper {
    private static int screenHeightInPx = -1;
    private static int screenWidthInPx = -1;

    public static int getScreenHeightInPx() {
        if (screenHeightInPx < 0) {
            init();
        }
        return screenHeightInPx;
    }

    public static int getScreenWidthInPx() {
        if (screenWidthInPx < 0) {
            init();
        }
        return screenWidthInPx;
    }

    private static void init() {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenHeightInPx = metrics.heightPixels;
        screenWidthInPx = metrics.widthPixels;
    }

    public static int dpToPx(int dp) {
        return (int) ((dp * MyApplication.getInstance().getResources().getDisplayMetrics().density) + 0.5);
    }
}
