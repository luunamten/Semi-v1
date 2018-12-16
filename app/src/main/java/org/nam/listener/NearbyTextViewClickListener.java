package org.nam.listener;

import android.view.View;

import org.nam.HomeActivity;
import org.nam.contract.Contract;

public class NearbyTextViewClickListener implements View.OnClickListener {
    private HomeActivity activity;
    public NearbyTextViewClickListener(HomeActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onClick(View v) {
        activity.loadAllNewStoresOrProducts();
    }
}
