package com.example.gadau.pricecheck.logic;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gadau.pricecheck.IdenticalInfoFragment;
import com.example.gadau.pricecheck.MainInfoFragment;
import com.example.gadau.pricecheck.data.DataItem;

import java.util.ArrayList;

public class InfoAdapter extends FragmentPagerAdapter {
    Context mContext;
    MainInfoFragment mMainInfoFragment;
    IdenticalInfoFragment mIdenticalInfoFragment;

    public InfoAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                mMainInfoFragment = new MainInfoFragment();
                return mMainInfoFragment;
            case 1:
                mIdenticalInfoFragment = new IdenticalInfoFragment();
                return mIdenticalInfoFragment;
            default:
                return null;
        }
    }

    public void updateData() {
        if (mIdenticalInfoFragment != null) {
            mIdenticalInfoFragment.refreshPage();
        }
        if (mMainInfoFragment != null) {
            mMainInfoFragment.refresh();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 1:
                return "Identicals";
            default:
                return "Main";
        }
    }
}
