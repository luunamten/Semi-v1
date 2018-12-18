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

    private FragmentCreator fragmentCreator;
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
        setupFragmentCreator();
    }

    private void setupFragmentCreator() {
        final Bundle networkError = new Bundle();
        final Bundle emptyError = new Bundle();
        final Bundle locationError = new Bundle();
        final Bundle loading = new Bundle();
        networkError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_trees);
        networkError.putString(ErrorFragment.MESSAGE, getString(R.string.networkErrorMessage));
        emptyError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_blank);
        emptyError.putString(ErrorFragment.MESSAGE, getString(R.string.emptyResultMessage));
        locationError.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_desert);
        locationError.putString(ErrorFragment.MESSAGE, getString(R.string.locationErrorMessage));
        loading.putInt(ErrorFragment.IMAGE_RESOURCE, R.drawable.ic_beach);
        loading.putString(ErrorFragment.MESSAGE, getString(R.string.loadMessage));
        fragmentCreator = new FragmentCreator(R.id.fragmentContainer,
                getChildFragmentManager());
        fragmentCreator.add(StoreViewFragment.class, (Bundle)null)
                .add(ProductViewFragment.class, (Bundle) null)
                .add(ErrorFragment.class, networkError)
                .add(ErrorFragment.class, emptyError)
                .add(ErrorFragment.class, locationError)
                .add(ErrorFragment.class, loading);
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
