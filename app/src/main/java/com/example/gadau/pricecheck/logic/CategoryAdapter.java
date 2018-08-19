package com.example.gadau.pricecheck.logic;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gadau.pricecheck.R;
import com.example.gadau.pricecheck.RestockFragment;
import com.example.gadau.pricecheck.SearchFragment;
import com.example.gadau.pricecheck.UnfinishedFragment;

public class CategoryAdapter extends FragmentPagerAdapter{
    private Context mContext;

    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new UnfinishedFragment();
            default:
                return new RestockFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return mContext.getString(R.string.header_search);
            case 1:
                return mContext.getString(R.string.header_new);
            default:
                return mContext.getString(R.string.header_restock);
        }
    }
}
