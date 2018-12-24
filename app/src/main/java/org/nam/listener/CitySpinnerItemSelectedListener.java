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

public class CitySpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public CitySpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        City selectedCity = (City) parent.getSelectedItem();
        Spinner districtSpinner = user.getDistrictSpinner();
        List<District> districts = new ArrayList<>();
        SharedPreferences dataStore = MyApp.getInstance().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = dataStore.edit();
        editor.putInt(Contract.SHARED_CITY_KEY, position);
        editor.apply();
        districts.add(new District(-1,
                view.getContext().getString(R.string.districtLabel)));
        if(selectedCity.getId() != -1) {
            final AddressDBConnector connector = AddressDBConnector.getInstance();
            districts.addAll(connector.getDistrictsFromCity(
                    selectedCity.getId()));
        }
        ArrayAdapter<District> adapter = new ArrayAdapter<District>(
                view.getContext(),
                android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);
        districtSpinner.setSelection(dataStore.getInt( Contract.SHARED_DISTRICT_KEY, 0));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
