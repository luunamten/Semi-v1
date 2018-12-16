package org.nam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nam.R;
import org.nam.object.Location;
import org.nam.object.Product;
import org.nam.custom.OnBottomReachedListener;
import org.nam.custom.OnItemClickListener;
import org.nam.custom.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductViewFragment extends Fragment {

    private ProductAdapter recyclerViewAdapter;
    private Listener listener;

    public static interface Listener {
        public void onProductItemClick(Product product);
        public void onProductScrollToLimit(Product product, int position);
    }

    public ProductViewFragment() {
        recyclerViewAdapter = new ProductAdapter(new ArrayList<Product>());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView productRecyclerView = view.findViewById(R.id.productRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        productRecyclerView.setHasFixedSize(true);
        productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(Object obj, int position) {
                listener.onProductScrollToLimit((Product)obj, position);
            }
        });
        recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Object obj) {
                listener.onProductItemClick((Product) obj);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateDataSet(List<Product> products, Location location) {
        recyclerViewAdapter.setDataSet(products, location);
    }

    public void addDataSet(List<Product> products, Location location) {
        recyclerViewAdapter.addDataSet(products, location);
    }

    public int getItemCount() {
        return recyclerViewAdapter.getItemCount();
    }
}