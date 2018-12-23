package org.nam.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.nam.object.City;
import org.nam.object.Country;
import org.nam.object.District;
import org.nam.sqlite.AddressDBConnector;

import java.util.List;

public class CitySpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public CitySpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        City selectedCity = (City) parent.getSelectedItem();
        List<District> districts = connector.getDistrictsFromCity(
                selectedCity.getId());
        ArrayAdapter<District> adapter = new ArrayAdapter<District>(
                view.getContext(),
                android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        user.getDistrictSpinner().setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
