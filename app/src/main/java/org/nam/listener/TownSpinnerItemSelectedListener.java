package org.nam.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.nam.MyApp;
import org.nam.R;
import org.nam.contract.Contract;
import org.nam.object.District;
import org.nam.object.Town;
import org.nam.sqlite.AddressDBConnector;

import java.util.ArrayList;
import java.util.List;

public class TownSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public TownSpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences dataStore = MyApp.getInstance().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putInt(Contract.SHARED_TOWN_KEY, position);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
