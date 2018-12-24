package org.nam.custom;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.nam.MyApp;
import org.nam.R;
import org.nam.contract.Contract;
import org.nam.object.Location;
import org.nam.object.Store;
import org.nam.util.MathUtils;
import org.nam.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private List<Store> stores;
    private Location location;
    private OnBottomReachedListener<Store> bottomReachedListener;
    private OnItemClickListener<Store> itemClickListener;


    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        private ImageView logo;
        private TextView titleTextView;
        private TextView addressTextView;
        private TextView ratingTextView;
        private TextView distanceTextView;
        private StoreViewHolder(View view) {
            super(view);
            logo = view.findViewById(R.id.storeRVLogo);
            titleTextView = view.findViewById(R.id.storeRVTitle);
            addressTextView = view.findViewById(R.id.storeRVAddress);
            ratingTextView = view.findViewById(R.id.storeRVRating);
            distanceTextView = view.findViewById(R.id.storeRVDistance);
        }

        private void setStore(Store store, String distanceStr) {
            String imageURL = store.getImageURL();
            Resources resources = MyApp.getInstance().getResources();
            float ratingValue = store.getRating();
            Location location = store.getGeo();
            titleTextView.setText(store.getTitle());
            addressTextView.setText(store.getAddress().toString());
            ratingTextView.setText(String.format("%.1f", ratingValue));
            distanceTextView.setText(distanceStr);
            if(ratingValue <= Contract.RATING_LOW_MAX && ratingValue >= Contract.RATING_LOW_MIN) {
                ratingTextView.setTextColor(resources.getColor(R.color.colorLowRating));
            } else if(ratingValue >= Contract.RATING_MEDIUM_MIN && ratingValue <= Contract.RATING_MEDIUM_MAX) {
                ratingTextView.setTextColor(resources.getColor(R.color.colorMediumRating));
            } else if(ratingValue >= Contract.RATING_HIGH_MIN && ratingValue <= Contract.RATING_HIGH_MAX) {
                ratingTextView.setTextColor(resources.getColor(R.color.colorHighRating));
            }
            if(!imageURL.equals("")) {

            }
        }
    }

    public StoreAdapter(List<Store> stores) {
        this.stores = stores;
    }

    public void setDataSet(List<Store> stores, @Nullable Location location) {
        this.stores = stores;
        this.location = location;
        notifyDataSetChanged();
    }

    public void addDataSet(Collection<Store> stores, @Nullable Location location) {
        int start = getItemCount();
        this.stores.addAll(stores);
        this.location = location;
        notifyItemRangeInserted(start, stores.size());
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.store_recyclerview_item, viewGroup, false);
        StoreViewHolder vh = new StoreViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder viewHolder, int i) {
        final Store store = stores.get(i);
        String distanceStr = "";
        if(location != null) {
            double distance = MathUtils.haversine(location, store.getGeo());
            distanceStr = StringUtils.toDistanceFormat(distance);
        }
        viewHolder.setStore(stores.get(i), distanceStr);
        if(bottomReachedListener != null && i == stores.size() - 1) {
            bottomReachedListener.onBottomReached(stores.get(i), i);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(store);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener<Store> listener) {
        this.bottomReachedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener<Store> listener) {
        this.itemClickListener = listener;
    }

    public String getLastStoreId() {
        return stores.get(stores.size() - 1).getId();
    }
}
