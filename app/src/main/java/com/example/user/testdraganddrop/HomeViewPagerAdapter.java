package com.example.user.testdraganddrop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/5/2015.
 */
public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<ItemGrid> data;
    private int cellWidth;
    private int cellHeight;

    public HomeViewPagerAdapter(FragmentManager fm, int cellWidth, int cellHeight) {
        super(fm);
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        data = new ArrayList<>();
        data.add(new ItemGrid(1, "Item 1", R.drawable.ic_temp));
        data.add(new ItemGrid(2, "Item 2", R.drawable.ic_temp2));
        data.add(new ItemGrid(3, "Item 3", R.drawable.ic_temp3));
        data.add(new ItemGrid(4, "Item 4", R.drawable.ic_temp4));
        for (int i = data.size(); i < 20; i++) {
            data.add(new ItemGrid(0, null, 0));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return HomeItemFragment.newInstance(data, cellWidth, cellHeight);
    }

    @Override
    public int getCount() {
        return 1;
    }
}
