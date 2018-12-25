package org.nam.minh;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.nam.R;
import org.nam.object.Product;

import java.util.Collection;
import java.util.List;

public class StoreDetailProductAdapter extends RecyclerView.Adapter<StoreDetailProductAdapter.ViewHolder> {
    private Context mRootContext;
    private List<Product> mData;
    private Dialog mDialogProduct;

    // data is passed into the constructor
    StoreDetailProductAdapter(Context mRootContext, List<Product> data) {
        this.mRootContext = mRootContext;
        mDialogProduct = new Dialog(mRootContext);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mRootContext);
        View view = mInflater.inflate(R.layout.minh_store_detail_list_product_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.store_detail_product_name.setText(mData.get(position).getTitle());
        holder.store_detail_product_image.setImageResource(R.drawable.ic_packing);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mDialogProduct.setContentView(R.layout.minh_store_detail_dialog_product);
                ImageView img_product = mDialogProduct.findViewById(R.id.store_detail_product_img_dialog);
                img_product.setImageResource(R.mipmap.minh_test_upload);
                TextView txt_product_name = mDialogProduct.findViewById(R.id.store_detail_product_name);
                txt_product_name.setText(mData.get(position).getTitle());
                TextView txt_product_type = mDialogProduct.findViewById(R.id.store_detail_product_type);
                txt_product_type.setText("Linh kiện điện tử: " + position);
                TextView txt_product_price = mDialogProduct.findViewById(R.id.store_detail_product_price);
                txt_product_price.setText("5.000.000" + " VND");
                TextView txt_product_description = mDialogProduct.findViewById(R.id.store_detail_product_description);
                txt_product_description.setText(position+ " -- Đây là mô tả: mô tả \na\na\na\n aaaa\na\na\na\na\na");
                Button btn_close_dialog_product = mDialogProduct.findViewById(R.id.store_detail_btn_close_product);
                btn_close_dialog_product.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogProduct.dismiss();
                    }
                });
                mDialogProduct.show();
            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(Collection<Product> data) {
        int length = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(length, data.size());
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView store_detail_product_name;
        ImageView store_detail_product_image;
        CardView product_item;
        private ItemClickListener itemClickListener;

        private ViewHolder(View itemView) {
            super(itemView);
            store_detail_product_name = itemView.findViewById(R.id.store_detail_product_name);
            store_detail_product_image = itemView.findViewById(R.id.store_detail_product_image);
            product_item = itemView.findViewById(R.id.product_item);
            itemView.setOnClickListener(this);
        }

        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onClick(view, getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public String getLastProductId() {
        if(mData.size() > 0) {
            return mData.get(mData.size() - 1).getId();
        }
        return "";
    }
}
