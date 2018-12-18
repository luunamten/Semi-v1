package org.nam.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nam.R;

public class SearchFragment extends Fragment {

    private FragmentHolder fragmentHolder;
    private static final int STORE_VIEW = 0;
    private static final int PRODUCT_VIEW = 1;
    private static final int NETWORK_ERR_VIEW = 2;
    private static final int EMPTY_ERR_VIEW = 3;
    private static final int LOCATION_ERR_VIEW = 4;
    private static final int LOAD_VIEW = 5;

    public static interface Listener {
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragmentHolder();
    }

    private void setupFragmentHolder() {
        ErrorFragment networkErrFragment = ErrorFragment.newInstance(R.drawable.ic_trees, R.string.networkErrorMessage);
        ErrorFragment emptyErrFragment = ErrorFragment.newInstance(R.drawable.ic_blank, R.string.emptyResultMessage);
        ErrorFragment locationErrFragment = ErrorFragment.newInstance(R.drawable.ic_desert, R.string.locationErrorMessage);
        ErrorFragment loadFragment = ErrorFragment.newInstance(R.drawable.ic_beach, R.string.loadMessage);
        fragmentHolder = new FragmentHolder(R.id.fragmentContainer,
                getChildFragmentManager());
        fragmentHolder.add(new StoreViewFragment())
                .add(new ProductViewFragment())
                .add(networkErrFragment)
                .add(emptyErrFragment)
                .add(locationErrFragment)
                .add(loadFragment);
        fragmentHolder.setCurrentFragment(LOCATION_ERR_VIEW);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
