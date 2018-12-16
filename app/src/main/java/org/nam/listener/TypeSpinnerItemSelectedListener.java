package org.nam.listener;

import android.location.Location;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.tasks.OnSuccessListener;

import org.nam.HomeActivity;
import org.nam.R;
import org.nam.firebase.IResult;

public class TypeSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private HomeActivity activity;
    public TypeSpinnerItemSelectedListener(HomeActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.loadAllNewStoresOrProducts();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
