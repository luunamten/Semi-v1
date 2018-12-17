package org.nam.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FragmentHolder {
    private List<Fragment> fragmentList;
    private int currentIndex;
    private int container;
    private FragmentManager fragmentManager;
    private boolean isStateLoss;
    public FragmentHolder(int container, FragmentManager fragmentManager) {
        this.container = container;
        fragmentList = new ArrayList<>();
        this.fragmentManager = fragmentManager;
        currentIndex = -1;
        isStateLoss = false;
    }

    public Fragment setCurrentFragment(int index) {
        if(index < 0 || fragmentList.size() <= index) {
            return null;
        }
        if (currentIndex == index && !isStateLoss) {
            return fragmentList.get(index);
        }
        currentIndex = index;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragmentList.get(index));
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        try {
            transaction.commit();
            isStateLoss = false;
        } catch (IllegalStateException exp) {
            isStateLoss = true;
            Log.w("my_error", exp.getMessage());
        }
        return fragmentList.get(index);
    }

    public FragmentHolder add(Fragment fragment) {
        if(fragment == null) {
            return this;
        }
        fragmentList.add(fragment);
        return this;
    }

    public Fragment getCurrentFragment() {
        if(currentIndex < 0 || currentIndex >= fragmentList.size()) {
            return null;
        }
        return fragmentList.get(currentIndex);
    }

    public Fragment getFragment(int index) {
        if(index < 0 || fragmentList.size() <= index) {
            return null;
        }
        return fragmentList.get(index);
    }

    public void recovery() {
        if(isStateLoss) {
            setCurrentFragment(currentIndex);
        }
    }
}
