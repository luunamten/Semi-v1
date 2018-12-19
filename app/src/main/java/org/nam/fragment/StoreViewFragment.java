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
import org.nam.object.Store;
import org.nam.custom.OnBottomReachedListener;
import org.nam.custom.OnItemClickListener;
import org.nam.custom.StoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class StoreViewFragment extends Fragment {

    private StoreAdapter recyclerViewAdapter;
    private Listener listener;

    public static interface Listener {
        public void onStoreItemClick(Store store);
        public void onStoreScrollToLimit(Store store, int position);
    }

    public StoreViewFragment() {
        recyclerViewAdapter = new StoreAdapter(new ArrayList<Store>());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_view, container, false);
        RecyclerView storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        storeRecyclerView.setHasFixedSize(true);
        storeRecyclerView.setNestedScrollingEnabled(false);
        storeRecyclerView.setLayoutManager(layoutManager);
        storeRecyclerView.setAdapter(recyclerViewAdapter);
        if(listener != null) {
            recyclerViewAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(Object obj, int position) {
                    listener.onStoreScrollToLimit((Store)obj, position);
                }
            });
            recyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(Object obj) {
                    listener.onStoreItemClick((Store) obj);
                }
            });;
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Listener) {
            listener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateDataSet(List<Store> stores, Location location) {
        recyclerViewAdapter.setDataSet(stores, location);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void addDataSet(List<Store> stores, Location location) {
        recyclerViewAdapter.addDataSet(stores, location);
        recyclerViewAdapter.notifyItemRangeInserted(
                recyclerViewAdapter.getItemCount(), stores.size());
    }

    public int getItemCount() {
        return recyclerViewAdapter.getItemCount();
    }

    public String getLastStoreId() {
        return recyclerViewAdapter.getLastStoreId();
    }
}
