package org.nam.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.nam.R;
import org.nam.object.City;
import org.nam.object.Country;
import org.nam.object.District;
import org.nam.sqlite.AddressDBConnector;

import java.util.ArrayList;
import java.util.List;

public class CountrySpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public CountrySpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        Country selectedCountry = (Country) parent.getSelectedItem();
        List<City> cities = new ArrayList<>();
        cities.add(new City(-1,
                view.getContext().getString(R.string.cityLabel)));
        cities.addAll(connector.getCitiesFromCountry(
                selectedCountry.getId()));
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(
                view.getContext(),
                android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        user.getCitySpinner().setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
