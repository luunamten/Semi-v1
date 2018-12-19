package org.nam.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.IResult;
import org.nam.firebase.StoreConnector;
import org.nam.object.IHaveIdAndName;
import org.nam.object.Location;
import org.nam.object.Store;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;
import org.nam.util.ObjectUtils;

import java.util.List;

public class StoreSearchFragment extends Fragment implements ISearch {

    private FragmentCreator fragmentCreator;
    private static final int STORE_VIEW = 0;
    private static final int NETWORK_ERR_VIEW = 1;
    private static final int EMPTY_ERR_VIEW = 2;
    private static final int LOAD_VIEW = 3;
    private StoreConnector storeConnector;
    private Location currentLocation;
    private LocationUtils locationUtils;
    private int type;
    private String query;

    @Override
    public void search(int type, String query) {
        this.type = type;
        this.query = query;
        searchStores(type, query);
    }

    @Override
    public void scroll(String lastId) {
        storeConnector.getStoresByKeywords(type, query, lastId,
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        StoreViewFragment fragment = (StoreViewFragment) fragmentCreator.getCurrentFragment();
                        if(!(fragment instanceof StoreViewFragment)) {
                            return;
                        }
                        if (result.size() == 0 && fragment.getItemCount() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        fragment.addDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) { }
                });
    }

    @Override
    public void clickItem(String id) {
        Log.w("Test_t", id);
    }

    public StoreSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragmentCreator();
        storeConnector = StoreConnector.getInstance();
        locationUtils = new LocationUtils();
        requestLocationUpdate();
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
        fragmentCreator.add(StoreViewFragment.class, (Bundle)null)
                .add(ErrorFragment.class, networkError)
                .add(ErrorFragment.class, emptyError)
                .add(ErrorFragment.class, loading);
        fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
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

    @Override
    public void onStart() {
        super.onStart();
        fragmentCreator.recovery();
    }

    public void searchStores(int storeType, String keywords) {
        fragmentCreator.setCurrentFragment(LOAD_VIEW);
        storeConnector.getStoresByKeywords(storeType, keywords, "",
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        if (result.size() == 0) {
                            fragmentCreator.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        StoreViewFragment fragment = (StoreViewFragment)
                                fragmentCreator.setCurrentFragmentNoArgs(STORE_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentCreator.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
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
}
