package org.nam.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.nam.R;
import org.nam.firebase.IResult;
import org.nam.firebase.ProductConnector;
import org.nam.listener.CitySpinnerItemSelectedListener;
import org.nam.listener.CountrySpinnerItemSelectedListener;
import org.nam.listener.DistrictSpinnerItemSelectedListener;
import org.nam.listener.IUseAddressSpinner;
import org.nam.listener.TownSpinnerItemSelectedListener;
import org.nam.object.City;
import org.nam.object.Country;
import org.nam.object.District;
import org.nam.object.IHaveIdAndName;
import org.nam.object.Location;
import org.nam.object.Product;
import org.nam.object.Town;
import org.nam.sqlite.AddressDBConnector;
import org.nam.util.LocationUtils;
import org.nam.util.ObjectUtils;

import java.util.List;

public class ProductSearchFragment extends Fragment implements ISearch,
        IUseAddressSpinner {
    private FragmentCreator fragmentCreator;
    private static final int PRODUCT_VIEW = 0;
    private static final int NETWORK_ERR_VIEW = 1;
    private static final int EMPTY_ERR_VIEW = 2;
    private static final int LOAD_VIEW = 3;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private Spinner districtSpinner;
    private Spinner townSpinner;
    private ProductConnector productConnector;
    private Location currentLocation;
    private LocationUtils locationUtils;
    private int type;
    private String query;

    @Override
    public void search(int type, String query, int mode) {
        this.type = type;
        this.query = query;
        searchProducts();
    }

    @Override
    public void scroll(String lastId) {
        productConnector.getProductsByKeywords(type, query, lastId,
                getAddress(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        Fragment fragment = fragmentCreator.getCurrentFragment();
                        if(!(fragment instanceof ProductViewFragment)) {
                            return;
                        }
                        ProductViewFragment storeFragment = (ProductViewFragment) fragment;
                        if (result.size() == 0 && storeFragment.getItemCount() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        storeFragment.addDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) { }
                });
    }

    @Override
    public void clickItem(String id) {
    }

    public ProductSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IUseFragment) {
            ((IUseFragment)context).onFragmentAttached(this);
        }
        setupFragmentCreator();
        productConnector = ProductConnector.getInstance();
        locationUtils = new LocationUtils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_search, container, false);
        countrySpinner = view.findViewById(R.id.countrySpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        townSpinner = view.findViewById(R.id.townSpinner);
        //event;
        countrySpinner.setOnItemSelectedListener(
                new CountrySpinnerItemSelectedListener(this));
        citySpinner.setOnItemSelectedListener(
                new CitySpinnerItemSelectedListener(this));
        districtSpinner.setOnItemSelectedListener(
                new DistrictSpinnerItemSelectedListener(this));
        townSpinner.setOnItemSelectedListener(
                new TownSpinnerItemSelectedListener(this));
        //init country spinner
        final AddressDBConnector connector = AddressDBConnector.getInstance();
        List<Country> countries = connector.getCountries();
        ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(
                inflater.getContext(), android.R.layout.simple_spinner_item,
                countries
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        return view;
    }

    private void setupFragmentCreator() {
        final Bundle networkError = new Bundle();
        final Bundle emptyError = new Bundle();
        final Bundle loading = new Bundle();
        networkError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_trees);
        networkError.putString(ErrorFragment.MESSAGE, getString(R.string.networkErrorMessage));
        emptyError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_blank);
        emptyError.putString(ErrorFragment.MESSAGE, getString(R.string.emptyResultMessage));
        loading.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_beach);
        loading.putString(ErrorFragment.MESSAGE, getString(R.string.loadMessage));
        fragmentCreator = new FragmentCreator(R.id.fragmentContainer,
                getChildFragmentManager());
        fragmentCreator.add(ProductViewFragment.class, (Bundle) null)
                .add(ErrorFragment.class, networkError)
                .add(ErrorFragment.class, emptyError)
                .add(ErrorFragment.class, loading);
        fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
    }

    public void searchProducts() {
        fragmentCreator.setCurrentFragment(LOAD_VIEW);
        productConnector.getProductsByKeywords(type, query, "",
                getAddress(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        if (result.size() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        ProductViewFragment fragment = (ProductViewFragment)
                                fragmentCreator.setCurrentFragmentNoArgs(PRODUCT_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentCreator.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    private Object[] getAddress() {
        Object[] address = {
                countrySpinner.getSelectedItem(),
                citySpinner.getSelectedItem(),
                districtSpinner.getSelectedItem(),
                townSpinner.getSelectedItem()
        };
        Object[] ids = new Object[4];
        for(int i = 0; i < address.length; i++) {
            if(address[i] == null) {
                ids[i] = -1;
            } else {
                ids[i] = ((IHaveIdAndName)address[i]).getId();
            }
        }
        return ids;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeLocationUpdates();
    }

    public void requestLocationUpdate() {
        locationUtils.requestLocationUpdates(new IResult<android.location.Location>() {
            @Override
            public void onResult(android.location.Location result) {
                if(result == null) {
                    currentLocation = null;
                } else {
                    currentLocation = ObjectUtils.toMyLocation(result);
                }
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }
    public void removeLocationUpdates() {
        locationUtils.removeLocationUpdates();
    }

    @Override
    public Spinner getCountrySpinner() {
        return countrySpinner;
    }

    @Override
    public Spinner getCitySpinner() {
        return citySpinner;
    }

    @Override
    public Spinner getDistrictSpinner() {
        return districtSpinner;
    }

    @Override
    public Spinner getTownSpinner() {
        return townSpinner;
    }
}
