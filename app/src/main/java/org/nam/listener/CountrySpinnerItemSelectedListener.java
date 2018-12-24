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
import org.nam.sqlite.AddressDBConnector;
import org.nam.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class CountrySpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private IUseAddressSpinner user;
    public CountrySpinnerItemSelectedListener(IUseAddressSpinner user) {
        this.user = user;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final Spinner citySpinner = user.getCitySpinner();
        if(citySpinner == null) {
            return;
        }
        final Country selectedCountry = (Country) parent.getSelectedItem();
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        final List<City> cities = new ArrayList<>();
        cities.add(new City(-1,
                view.getContext().getString(R.string.cityLabel)));
        cities.addAll(connector.getCitiesFromCountry(
                selectedCountry.getId()));
        final ArrayAdapter<City> adapter = new ArrayAdapter<City>(
                view.getContext(),
                android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
        final SharedPreferences dataStore = MyApp.getInstance().getSharedPreferences(Contract.SHARED_MY_STATE,
                Context.MODE_PRIVATE);
        if(position != dataStore.getInt(Contract.SHARED_COUNTRY_KEY, 0)) {
            final SharedPreferences.Editor editor = dataStore.edit();
            editor.putInt(Contract.SHARED_COUNTRY_KEY, position);
            editor.putInt(Contract.SHARED_CITY_KEY, 0);
            editor.putInt(Contract.SHARED_DISTRICT_KEY, 0);
            editor.putInt(Contract.SHARED_TOWN_KEY, 0);
            editor.apply();
        } else {
            citySpinner.setSelection(dataStore.getInt(Contract.SHARED_CITY_KEY, 0));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
