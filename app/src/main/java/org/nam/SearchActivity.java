package org.nam;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import com.google.android.gms.location.LocationSettingsResponse;

import org.nam.contract.Contract;
import org.nam.custom.MyFragmentAdapter;
import org.nam.firebase.IResult;
import org.nam.fragment.ISearch;
import org.nam.fragment.MyMapFragment;
import org.nam.fragment.ProductSearchFragment;
import org.nam.fragment.ProductViewFragment;
import org.nam.fragment.StoreSearchFragment;
import org.nam.fragment.StoreViewFragment;
import org.nam.object.Product;
import org.nam.object.Store;
import org.nam.util.LocationUtils;
import org.nam.util.StringUtils;

public class SearchActivity extends AppCompatActivity implements StoreViewFragment.Listener,
        ProductViewFragment.Listener {
    private final static int SEARCH_FRAGMENT = 0;
    private final static int MAP_FRAGMENT = 1;
    private int mode;
    private int typeId;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyFragmentAdapter fragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Intent intent = getIntent();
        setupActionBar(intent);
        mode = intent.getIntExtra(Contract.BUNDLE_MODE_KEY, -1);
        typeId = intent.getIntExtra(Contract.BUNDLE_MODE_TYPE_KEY, -1);
        //TabLayout, ViewPager
        tabLayout = findViewById(R.id.tabLayout);
        if(!setupViewPager()) {
            finish();
        }
        tabLayout.setupWithViewPager(viewPager);
        checkAndRequestPermission();
    }

    private void setupActionBar(Intent intent) {
        final int logoResource = intent.getIntExtra(Contract.BUNDLE_ACTION_LOGO_KEY, -1);
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(logoResource);
    }

    private boolean setupViewPager() {
        viewPager = findViewById(R.id.viewPaper);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        Fragment searchFragment = null;
        if(mode == Contract.STORE_MODE) {
            searchFragment = new StoreSearchFragment();
        } else if(mode == Contract.PRODUCT_MODE) {
            searchFragment = new ProductSearchFragment();
        } else {
            return false;
        }
        MyMapFragment mapFragment = new MyMapFragment();
        fragmentAdapter.addFragment(searchFragment, getString(R.string.searchTabText))
                .addFragment(mapFragment, getString(R.string.nearbyTabText));
        viewPager.setAdapter(fragmentAdapter);
        return true;
    }

    private void setupSearchView(Menu menu) {
        final Intent intent = getIntent();
        final int hintResource = intent.getIntExtra(Contract.BUNDLE_SEARCH_HINT_KEY, -1);
        final MenuItem searchItem = menu.findItem(R.id.actionSearchItem);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(hintResource));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                ISearch iSearch = (ISearch) fragmentAdapter.getItem(SEARCH_FRAGMENT);
                iSearch.search(typeId, StringUtils.normalize(s));
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        //expand SearchView first time
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() { searchItem.expandActionView(); }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        setupSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int request, String[] permission,
                                           int[] results) {
        super.onRequestPermissionsResult(request, permission, results);
        if(request == LocationUtils.REQUEST_LOCATION_PERMISSION_CODE) {
            if(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            }
        }
    }

    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        super.onActivityResult(request, result, intent);
        if(result == Activity.RESULT_OK) {
            if(request == LocationUtils.RESOLUTION_CODE) {
                onLocationSettingsOK();
            }
        }
    }

    private void checkAndRequestPermission() {
        if(LocationUtils.checkAndRequestPermission(this)) {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        LocationUtils.checkAndRequestLocationSettings(this,
                new IResult<LocationSettingsResponse>() {
            @Override
            public void onResult(LocationSettingsResponse result) {
                onLocationSettingsOK();
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    private void onLocationSettingsOK() {

    }

    @Override
    public void onProductItemClick(Product product) {

    }

    @Override
    public void onProductScrollToLimit(Product product, int position) {
        ISearch fragment = (ISearch) fragmentAdapter.getItem(SEARCH_FRAGMENT);
        fragment.scroll(product.getId());
    }

    @Override
    public void onStoreItemClick(Store store) {
        
    }

    @Override
    public void onStoreScrollToLimit(Store store, int position) {
        ISearch fragment = (ISearch) fragmentAdapter.getItem(SEARCH_FRAGMENT);
        fragment.scroll(store.getId());
    }
}
