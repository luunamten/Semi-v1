package org.nam.listener;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import org.nam.HomeActivity;
import org.nam.R;
import org.nam.contract.Contract;
import org.nam.fragment.ProductViewFragment;
import org.nam.fragment.StoreViewFragment;
import org.nam.object.IHaveIdAndName;
import org.nam.object.Product;
import org.nam.object.Store;
import org.nam.util.ObjectUtils;

import java.util.List;

public class ModeSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private HomeActivity activity;
    public ModeSpinnerItemSelectedListener(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case Contract.STORE_MODE:
                //Contract.STORE_MODE == HomeActivity.STORE_VIEW
                setFragmentAndNearbyLabel(Contract.STORE_MODE,
                        R.string.nearbyStoreLabel);
                loadStoreTypesToSpinner();
                break;
            case Contract.PRODUCT_MODE:
                //Contract.PRODUCT_MODE == HomeActivity.PRODUCT_VIEW
                setFragmentAndNearbyLabel(Contract.PRODUCT_MODE,
                        R.string.nearbyProductLabel);
                loadProductTypesToSpinner();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void setFragmentAndNearbyLabel(int framentIndex, int label) {
        activity.getNearbyTextView().setText(label);
        activity.getFragmentHolder().setCurrentFragment(framentIndex);
    }

    private void loadStoreTypesToSpinner() {
        List<Store.Type> storeTypeList = ObjectUtils.getStoreTypes();
        Store.Type[] storeTypes = new Store.Type[storeTypeList.size()];
        storeTypeList.toArray(storeTypes);
        ArrayAdapter<IHaveIdAndName> adapter = new ArrayAdapter<IHaveIdAndName>(activity,
                android.R.layout.simple_spinner_item, storeTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.getTypeSpinner().setAdapter(adapter);
    }

    private void loadProductTypesToSpinner() {
        List<Product.Type> productTypeList = ObjectUtils.getProductTypes();
        Product.Type[] productTypes = new Product.Type[productTypeList.size()];
        productTypeList.toArray(productTypes);
        ArrayAdapter<IHaveIdAndName> adapter= new ArrayAdapter<IHaveIdAndName>(activity,
                android.R.layout.simple_spinner_item, productTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.getTypeSpinner().setAdapter(adapter);
    }
}
