package com.example.myapplication.ui.subscription;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SubscriptionPagerAdapter extends FragmentStateAdapter {

    public SubscriptionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SubscriptionBrowseFragment();
            case 1:
                return new MySubscriptionsFragment();
            default:
                return new SubscriptionBrowseFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Browse and My Subscriptions
    }
}