package com.go.go;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public  final class FragmentAdatper extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    public FragmentAdatper(List<Fragment>fragments, FragmentManager fragmentManager){
        super(fragmentManager);
        this.mFragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
