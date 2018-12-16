package org.nam.custom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.nam.R;
import org.nam.object.Location;
import org.nam.object.Product;
import org.nam.util.MathUtils;
import org.nam.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    private List<Product> products;
    private List<Double> distances;
    private Location location;
    private OnBottomReachedListener bottomReachedListener;
    private OnItemClickListener itemClickListener;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public static class ProductHolder extends RecyclerView.ViewHolder {
        private ImageView logo;
        private TextView titleTextView;
        private TextView addressTextView;
        private TextView costTextView;
        private TextView distanceTextView;
        private ProductHolder(View view) {
            super(view);
            logo = view.findViewById(R.id.productRVLogo);
            titleTextView = view.findViewById(R.id.productRVTitle);
            addressTextView = view.findViewById(R.id.productRVAddress);
            costTextView = view.findViewById(R.id.productRVCost);
            distanceTextView = view.findViewById(R.id.productRVDistance);
        }
        private void setProduct(Product product, String distanceStr) {
            String imageURL = product.getImageURL();
            titleTextView.setText(product.getTitle());
            addressTextView.setText(product.getStore().getAddress().toString());
            costTextView.setText(StringUtils.toVNDCurrency(product.getCost()));
            distanceTextView.setText(distanceStr);
            if(!imageURL.equals("")) {

            }
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder viewHolder, int i) {
        final Product product = products.get(i);
        if(distances.get(i) < 0) {
            distances.set(i, MathUtils.haversine(location, product.getStore().getGeo()));
        }
        String distanceStr = StringUtils.toDistanceFormat(distances.get(i));
        viewHolder.setProduct(product, distanceStr);
        if(bottomReachedListener != null && i == products.size() - 1) {
            bottomReachedListener.onBottomReached(products.get(i), i);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(product);
            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.product_recyclerview_item1, viewGroup, false);
        ProductHolder holder = new ProductHolder(view);
        return holder;
    }

    public void setDataSet(List<Product> products, Location location) {
        this.products = products;
        this.location = location;
        distances = new ArrayList<>(Collections.nCopies(products.size(), -1.0));
        notifyDataSetChanged();
    }

    public void addDataSet(Collection<Product> products, Location location) {
        this.products.addAll(products);
        this.location = location;
        distances.addAll(Collections.nCopies(products.size(), -1.0));
        notifyItemRangeInserted(this.products.size(), products.size());
    }

    public void setOnBottomReachedListener(OnBottomReachedListener listener) {
        this.bottomReachedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}