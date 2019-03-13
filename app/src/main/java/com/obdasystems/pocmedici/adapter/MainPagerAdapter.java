package com.obdasystems.pocmedici.adapter;



import com.obdasystems.pocmedici.fragment.HomeFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private final int tabCount;

    public MainPagerAdapter(FragmentManager fm) {
        this(fm, 0);
    }

    public MainPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new HomeFragment();
            case 3:
                return new HomeFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
