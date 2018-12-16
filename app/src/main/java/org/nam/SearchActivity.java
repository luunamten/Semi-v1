package org.nam;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import org.nam.contract.Contract;
import org.nam.custom.MyFragmentAdapter;
import org.nam.fragment.ErrorFragment;

public class SearchActivity extends AppCompatActivity {
    private int mode;
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
        //TabLayout, ViewPager
        tabLayout = findViewById(R.id.tabLayout);
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
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

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPaper);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        ErrorFragment error1 = new ErrorFragment();
        ErrorFragment error2 = new ErrorFragment();
        error1.setArguments(R.drawable.ic_beach, R.string.loadMessage);
        error2.setArguments(R.drawable.ic_trees, R.string.networkErrorMessage);
        fragmentAdapter.addFragment(error1, getString(R.string.searchTabText))
                .addFragment(error2, getString(R.string.nearbyTabText));
        viewPager.setAdapter(fragmentAdapter);
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
}
