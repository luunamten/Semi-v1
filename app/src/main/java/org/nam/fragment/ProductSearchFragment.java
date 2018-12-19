package org.nam.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nam.R;

public class ProductSearchFragment extends Fragment implements ISearch {
    private FragmentCreator fragmentCreator;
    private static final int PRODUCT_VIEW = 0;
    private static final int NETWORK_ERR_VIEW = 1;
    private static final int EMPTY_ERR_VIEW = 2;
    private static final int LOAD_VIEW = 3;

    @Override
    public void search(int type, String query) {

    }

    @Override
    public void scroll(String lastId) {

    }

    @Override
    public void clickItem(String id) {
        Log.w("Test_t", id);
    }

    public ProductSearchFragment() {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
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
