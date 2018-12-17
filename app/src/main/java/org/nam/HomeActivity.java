package org.nam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.contract.Contract;
import org.nam.custom.MyFragmentAdapter;
import org.nam.firebase.IResult;
import org.nam.firebase.ProductConnector;
import org.nam.firebase.StoreConnector;
import org.nam.fragment.ErrorFragment;
import org.nam.fragment.FragmentHolder;
import org.nam.fragment.ProductViewFragment;
import org.nam.fragment.StoreViewFragment;
import org.nam.listener.ModeSpinnerItemSelectedListener;
import org.nam.listener.NearbyTextViewClickListener;
import org.nam.listener.TypeSpinnerItemSelectedListener;
import org.nam.object.IHaveIdAndName;
import org.nam.object.Product;
import org.nam.object.Store;
import org.nam.util.ObjectUtils;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements StoreViewFragment.Listener,
        ProductViewFragment.Listener {
    //Code
    private static final int CHECK_LOCATION_SETTINGS_CODE = 0;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private static final String FRAGMENT_TAG = "fragment";
    private static final int STORE_VIEW = 0;
    private static final int PRODUCT_VIEW = 1;
    private static final int NETWORK_ERR_VIEW = 2;
    private static final int EMPTY_ERR_VIEW = 3;
    private static final int LOCATION_ERR_VIEW = 4;
    private static final int LOAD_VIEW = 5;
    //View
    private AppCompatSpinner searchModeSpinner;
    private AppCompatSpinner typeSpinner;
    private ModeSpinnerItemSelectedListener modeListener;
    //this Fragment's desired to be attached to Activity, but not sure it's attached
    private TextView nearbyTextView;
    private Toolbar toolbar;
    private FragmentHolder fragmentHolder;
    //Firebase connector
    private StoreConnector storeConnector;
    private ProductConnector productConnector;
    //Location
    private FusedLocationProviderClient locationProvider;
    private org.nam.object.Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Set Toolbar for ActionBar
        setupActionBar();
        //init
        searchModeSpinner = findViewById(R.id.searchModeSpinner);
        typeSpinner = findViewById(R.id.typesSpinner);
        nearbyTextView = findViewById(R.id.nearbyTextView);
        modeListener = new ModeSpinnerItemSelectedListener(this);
        storeConnector = StoreConnector.getInstance();
        productConnector = ProductConnector.getInstance();
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        setupFragmentHolder();
        //event, permission, config
        searchModeSpinner.setOnItemSelectedListener(modeListener);
        typeSpinner.setOnItemSelectedListener(new TypeSpinnerItemSelectedListener(this));
        nearbyTextView.setOnClickListener(new NearbyTextViewClickListener(this));
        checkAndRequestPermission();
    }

    private void setupFragmentHolder() {
        ErrorFragment networkErrFragment = new ErrorFragment();
        ErrorFragment emptyErrFragment = new ErrorFragment();
        ErrorFragment locationErrFragment = new ErrorFragment();
        ErrorFragment loadFragment = new ErrorFragment();
        networkErrFragment.setArguments(R.drawable.ic_trees, R.string.networkErrorMessage);
        emptyErrFragment.setArguments(R.drawable.ic_blank, R.string.emptyResultMessage);
        locationErrFragment.setArguments(R.drawable.ic_desert, R.string.locationErrorMessage);
        loadFragment.setArguments(R.drawable.ic_beach, R.string.loadMessage);
        fragmentHolder = new FragmentHolder(R.id.homeFragmentContainer,
                getSupportFragmentManager());
        fragmentHolder.add(new StoreViewFragment())
                .add(new ProductViewFragment())
                .add(networkErrFragment)
                .add(emptyErrFragment)
                .add(locationErrFragment)
                .add(loadFragment);
    }

    private void setupActionBar() {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_actionbar_logo);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public AppCompatSpinner getModeSpinner() {
        return searchModeSpinner;
    }

    public AppCompatSpinner getTypeSpinner() {
        return typeSpinner;
    }

    public TextView getNearbyTextView() {
        return nearbyTextView;
    }

    public FragmentHolder getFragmentHolder() {
        return fragmentHolder;
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int locationResult = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);
                return;
            }
        }
        onPermissionGranted();
    }

    private void onPermissionGranted() {
        checkAndRequestLocationSettings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }

    private void checkAndRequestLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Contract.LOCATION_INTERVAL)
                .setFastestInterval(Contract.LOCATION_FASTEST_INTERVAL);
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(request);
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                onSettingsOK();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final ResolvableApiException rexp = (ResolvableApiException) e;
                try {
                    rexp.startResolutionForResult(HomeActivity.this, CHECK_LOCATION_SETTINGS_CODE);
                } catch (IntentSender.SendIntentException sexp) {
                    Log.w("my_error", sexp);
                }
            }
        });
    }

    private void onSettingsOK() {
        loadFirst();
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if (result == RESULT_OK) {
            if (requestCode == CHECK_LOCATION_SETTINGS_CODE) {
                onSettingsOK();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(final IResult<Location> result) {
        final Task<Location> task = locationProvider.getLastLocation();
        task.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
                    return;
                }
                currentLocation = ObjectUtils.toMyLocation(location);
                result.onResult(location);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void loadFirst() {
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Contract.LOCATION_INTERVAL)
                .setFastestInterval(Contract.LOCATION_FASTEST_INTERVAL);
        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                final Location location = locationResult.getLastLocation();
                if (location != null) {
                    locationProvider.removeLocationUpdates(this);
                    currentLocation = ObjectUtils.toMyLocation(location);
                    final int modePosition = searchModeSpinner.getSelectedItemPosition();
                    if (modePosition == Contract.STORE_MODE) {
                        loadAllNewStores();
                    } else if (modePosition == Contract.PRODUCT_MODE) {
                        loadAllNewProducts();
                    }
                }
            }
        };
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    //load from offset 0,discard old data in RecyclerView and load new data to it.
    public void loadAllNewStores() {
        if (currentLocation == null) {
            fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.STORE_MODE) {
            return;
        }
        IHaveIdAndName selectedItem = ((IHaveIdAndName) typeSpinner.getSelectedItem());
        if (selectedItem == null) {
            return;
        }
        fragmentHolder.setCurrentFragment(LOAD_VIEW);
        storeConnector.getNearbyStores(currentLocation, 0, selectedItem.getId(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.STORE_MODE) {
                            return;
                        }
                        if (result.size() == 0) {
                            fragmentHolder.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        StoreViewFragment fragment = (StoreViewFragment) fragmentHolder.setCurrentFragment(STORE_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentHolder.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    //load from offset 0,discard old data in RecyclerView and load new data to it.
    public void loadAllNewProducts() {
        if (currentLocation == null) {
            fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.PRODUCT_MODE) {
            return;
        }
        IHaveIdAndName selectedItem = ((IHaveIdAndName) typeSpinner.getSelectedItem());
        if (selectedItem == null) {
            return;
        }
        fragmentHolder.setCurrentFragment(LOAD_VIEW);
        productConnector.getNearbyProducts(currentLocation, 0, selectedItem.getId(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.PRODUCT_MODE) {
                            return;
                        }
                        if (result.size() == 0) {
                            fragmentHolder.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        ProductViewFragment fragment = (ProductViewFragment) fragmentHolder.setCurrentFragment(PRODUCT_VIEW);
                        fragment.updateDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                        fragmentHolder.setCurrentFragment(NETWORK_ERR_VIEW);
                    }
                });
    }

    private void loadProductsAt(int index) {
        if (currentLocation == null) {
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.PRODUCT_MODE) {
            return;
        }
        final IHaveIdAndName selectedItem = ((IHaveIdAndName) typeSpinner.getSelectedItem());
        if (selectedItem == null) {
            return;
        }
        productConnector.getNearbyProducts(currentLocation, index, selectedItem.getId(),
                new IResult<List<Product>>() {
                    @Override
                    public void onResult(List<Product> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.PRODUCT_MODE) {
                            return;
                        }
                        ProductViewFragment fragment =
                                (ProductViewFragment) fragmentHolder.getFragment(PRODUCT_VIEW);
                        if (result.size() == 0 && fragment.getItemCount() == 0) {
                            fragmentHolder.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        fragmentHolder.setCurrentFragment(PRODUCT_VIEW);
                        fragment.addDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                    }
                });
    }

    private void loadStoresAt(int position) {
        if (currentLocation == null) {
            return;
        }
        if (searchModeSpinner.getSelectedItemPosition()
                != Contract.STORE_MODE) {
            return;
        }
        IHaveIdAndName selectedItem = ((IHaveIdAndName) typeSpinner.getSelectedItem());
        if (selectedItem == null) {
            return;
        }
        storeConnector.getNearbyStores(currentLocation, position, selectedItem.getId(),
                new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {
                        //when network error, or some error occur, currentFragment is replaced with ErrorFragment
                        //we must assign currentFragment to appropriate fragment
                        if (searchModeSpinner.getSelectedItemPosition()
                                != Contract.STORE_MODE) {
                            return;
                        }
                        StoreViewFragment fragment =
                                (StoreViewFragment) fragmentHolder.getFragment(STORE_VIEW);
                        if (result.size() == 0 && fragment.getItemCount() == 0) {
                            fragmentHolder.setCurrentFragment(EMPTY_ERR_VIEW);
                            return;
                        }
                        fragment = (StoreViewFragment) fragmentHolder.setCurrentFragment(STORE_VIEW);
                        fragment.addDataSet(result, currentLocation);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) {
                    }
                });
    }

    @Override
    public void onStoreItemClick(Store store) {

    }

    @Override
    public void onStoreScrollToLimit(Store store, final int position) {
        loadStoresAt(position + 1);
    }

    @Override
    public void onProductItemClick(Product product) {
    }

    @Override
    public void onProductScrollToLimit(Product product, final int position) {
        loadProductsAt(position + 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSearchItem:
                launchSearchActivity();
                break;
        }
        return true;
    }

    private void launchSearchActivity() {
        final int mode = searchModeSpinner.getSelectedItemPosition();
        final Intent intent = new Intent(this, SearchActivity.class);
        if(mode == Contract.STORE_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.STORE_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.storeSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_shop);
        } else if(mode == Contract.PRODUCT_MODE) {
            intent.putExtra(Contract.BUNDLE_MODE_KEY, Contract.PRODUCT_MODE);
            intent.putExtra(Contract.BUNDLE_SEARCH_HINT_KEY, R.string.productSearchHint);
            intent.putExtra(Contract.BUNDLE_ACTION_LOGO_KEY, R.drawable.ic_actionbar_product);
        } else {
            return;
        }
        final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                this, toolbar, "Toolbar"
        );
        startActivity(intent, options.toBundle());
    }

    public void loadAllNewStoresOrProducts() {
        getLastLocation(new IResult<Location>() {
            @Override
            public void onResult(@NonNull Location location) {
                if (getModeSpinner().getSelectedItemPosition() == 0) { //it's store
                    loadAllNewStores();
                } else { //it's Product
                    loadAllNewProducts();
                }
            }
            @Override
            public void onFailure(@NonNull Exception exp) {
                fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentHolder.recovery();
    }
}
