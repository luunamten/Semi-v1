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
import org.nam.object.City;
import org.nam.object.Country;
import org.nam.object.District;
import org.nam.object.Town;
import org.nam.sqlite.AddressDBConnector;
import org.nam.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class DistrictSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public DistrictSpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        District selectedDistrict = (District) parent.getSelectedItem();
        Spinner townSpinner = user.getTownSpinner();
        List<Town> towns = new ArrayList<>();
        SharedPreferences dataStore = MyApp.getInstance().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putInt(Contract.SHARED_DISTRICT_KEY, position);
        editor.apply();
        towns.add(new Town(-1,
                view.getContext().getString(R.string.townLabel)));
        if(selectedDistrict.getId() != -1) {
            final AddressDBConnector connector = AddressDBConnector.getInstance();
            towns.addAll(connector.getTownsFromDistrict(
                    selectedDistrict.getId()));
        }
        ArrayAdapter<Town> adapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_item, towns);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        townSpinner.setAdapter(adapter);
        townSpinner.setSelection(dataStore.getInt(Contract.SHARED_TOWN_KEY, 0));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
