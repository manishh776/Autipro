package com.autipro.adapters;

import com.autipro.fragments.ContactUsFragment;
import com.autipro.fragments.LiveFragment;
import com.autipro.fragments.SettingsFragment;
import com.autipro.fragments.StatisticsFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewpagerAdapter extends FragmentStatePagerAdapter {

    private final int SCREEN_COUNT = 4;

    public ViewpagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new SettingsFragment();
                break;

            case 1:
                fragment = new LiveFragment();
                break;

            case 2:
                fragment = new StatisticsFragment();
                break;

            case 3:
                fragment = new ContactUsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return SCREEN_COUNT;
    }
}
