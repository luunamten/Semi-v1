package org.nam.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.nam.R;
import org.nam.object.City;
import org.nam.object.Country;
import org.nam.object.Town;
import org.nam.sqlite.AddressDBConnector;

import java.util.List;

public class DistrictSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public DistrictSpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        Country selectedDistrict = (Country) parent.getSelectedItem();
        List<Town> towns = connector.getTownsFromDistrict(
                selectedDistrict.getId());
        ArrayAdapter<Town> adapter = new ArrayAdapter<Town>(
                view.getContext(),
                android.R.layout.simple_spinner_item, towns);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        user.getTownSpinner().setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
