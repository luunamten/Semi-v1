package org.nam.minh;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.IResult;
import org.nam.firebase.ProductConnector;
import org.nam.firebase.StoreConnector;
import org.nam.minh.object.Comment;
import org.nam.object.Product;
import org.nam.object.Store;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;
import org.nam.util.ObjectUtils;
import org.nam.util.StringUtils;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreDetailActivity extends AppCompatActivity {
    private Toolbar toolbar_store_detail;
    private TextView toolbar_store_detail_name;
    private TextView store_detail_name, store_detail_address, store_detail_type, store_detail_state, store_detail_time,
            store_detail_distance, store_detail_description, store_detail_total_product, store_detail_total_comment,
            store_detail_total_rating;
    private LinearLayout store_detail_utilities;
    private StoreDetailProductAdapter mProductAdapter;
    private StoreDetailCommentAdapter mCommentAdapter;
    private RecyclerView mRecyclerProduct, mRecyclerComment;
    private List<Comment> mListComment;
    private Dialog mDialogUtility;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_store_detail);
        initView();
        getStoreData();
    }

    private void initView() {
        toolbar_store_detail = findViewById(R.id.toolbar_store_detail);
        toolbar_store_detail.setTitle("");
        setSupportActionBar(toolbar_store_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar_store_detail_name = findViewById(R.id.toolbar_store_detail_name);
        store_detail_type = findViewById(R.id.store_detail_type);
        store_detail_name = findViewById(R.id.store_detail_name);
        store_detail_address = findViewById(R.id.store_detail_address);
        store_detail_total_product = findViewById(R.id.store_detail_total_product);
        store_detail_total_comment = findViewById(R.id.store_detail_total_comment);
        store_detail_total_rating = findViewById(R.id.store_detail_total_rating);
        store_detail_state = findViewById(R.id.store_detail_state);
        store_detail_time = findViewById(R.id.store_detail_time);
        store_detail_distance = findViewById(R.id.store_detail_distance);
        store_detail_description = findViewById(R.id.store_detail_description);
        store_detail_utilities = findViewById(R.id.store_detail_utilities);
        //RecyclerView product
        mRecyclerProduct = findViewById(R.id.store_detail_list_product);
        mRecyclerProduct.setLayoutManager(new GridLayoutManager(this, 3));
        mProductAdapter = new StoreDetailProductAdapter(this, new ArrayList<Product>());
        mRecyclerProduct.setAdapter(mProductAdapter);

        //region init list comment
        mListComment = new ArrayList<>();
        mListComment.add(new Comment("1", "Minh", R.drawable.minh_ic_home_around_me, "comment 1 đây", 5, "01/12/2018 23:22"));
        mListComment.add(new Comment("2", "Nam", R.mipmap.minh_test_upload, "comment 2 đây", 4, "02/12/2018 23:22"));
        mListComment.add(new Comment("1", "Minh", R.drawable.minh_ic_home_around_me, "comment 1 đây", 2, "01/12/2018 23:22"));
        if (mListComment.size() == 0) {
            /*Toast.makeText(this, "a", Toast.LENGTH_SHORT).show();
            img_error_list = findViewById(R.id.store_detail_error_list);
            img_error_list.setImageResource(R.drawable.ic_blank);*/
            TextView txt_error_comment = findViewById(R.id.txt_error_comment);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 20, 10, 20);
            txt_error_comment.setLayoutParams(params);

            txt_error_comment.setText(R.string.store_detail_error_comment_mess);
            txt_error_comment.setTextSize(getResources().getDimension(R.dimen.store_detail_error_list_text_size));
            txt_error_comment.setLines(2);
        } else {
            mRecyclerComment = findViewById(R.id.store_detail_list_comment);
            mRecyclerComment.setLayoutManager(new LinearLayoutManager(this));
            mCommentAdapter = new StoreDetailCommentAdapter(this, mListComment);
            mRecyclerComment.setAdapter(mCommentAdapter);
        }
        //endregion

    }

    private void setError() {
        TextView txt_error_list = findViewById(R.id.txt_error_list);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 20, 10, 20);
        txt_error_list.setLayoutParams(params);
        txt_error_list.setText(R.string.store_detail_error_list_mess);
        txt_error_list.setTextSize(getResources().getDimension(R.dimen.store_detail_error_list_text_size));
        txt_error_list.setLines(2);
    }

    private void getStoreData() {
        StoreConnector connector = StoreConnector.getInstance();
        Intent intent = getIntent();
        String storeId = intent.getStringExtra(Contract.BUNDLE_STORE_ID_KEY);
        connector.getStoreById(storeId, new IResult<Store>() {
            @Override
            public void onResult(Store result) {
                if(result != null) {
                    store = result;
                    putDataToView();
                    getProducts();
                }
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    private void putDataToView() {
        toolbar_store_detail_name.setText(store.getName());
        store_detail_name.setText(store.getName());
        store_detail_address.setText(store.getAddress().toString());
        store_detail_type.setText(String.format(" - %s", store.getType().getName()));
        store_detail_time.setText(store.getStartEnd());
        store_detail_description.setText(store.getDescription());
        store_detail_total_rating.setText(String.format("%.1f", store.getRating()));
        //set color for opened or closed
        if (isOpened(store.getStartEnd())) {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_open));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_green_500));
        } else {
            store_detail_state.setText(getResources().getString(R.string.store_detail_state_close));
            store_detail_state.setTextColor(getResources().getColor(R.color.material_red_a200));
        }
        //set color for rating value
        float rating = store.getRating();
        int loopLimit = Contract.RATING_LEVELS.length - 1;
        for(int i = 0; i < loopLimit; i++) {
            if(Contract.RATING_LEVELS[i] <= rating && Contract.RATING_LEVELS[i + 1] > rating) {
                store_detail_total_rating.setTextColor(Contract.RATING_COLORS[i]);
            }
        }
        //set distance
        LocationUtils.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double distance = MathUtils.haversine(ObjectUtils.toMyLocation(location),
                            store.getGeo());
                    store_detail_distance.setText(StringUtils.toDistanceFormat(distance));
                } else {
                    store_detail_distance.setText("");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { }
        });
        //set utilities
        List<Store.Utility> utilities = store.getUtilities();
        int numberOfUtilities = utilities.size();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        for(int i = 0; i < numberOfUtilities; i++) {
            ImageView img_utility = new ImageView(this);
            img_utility.setImageResource(Contract.UTILITY_RESOURCES[i]);
            img_utility.setLayoutParams(lp);
            store_detail_utilities.addView(img_utility);
        }
    }

    private boolean isOpened(String startEndStr) {
        String[] array = startEndStr.split("-");
        String startStr = array[0];
        String endStr = array[1];
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String nowStr = formatter.format(Calendar.getInstance().getTime());
        try {
            Date now = formatter.parse(nowStr);
            Date startTime = formatter.parse(startStr);
            Date endTime = formatter.parse(endStr);
            return now.after(startTime) && now.before(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getNumberOfProduct() {
        StoreConnector connector = StoreConnector.getInstance();
    }

    private void getProducts() {
        ProductConnector connector = ProductConnector.getInstance();
        connector.getProductsOfStore(store.getId(), mProductAdapter.getLastProductId(),
                new IResult<List<org.nam.object.Product>>() {
            @Override
            public void onResult(List<org.nam.object.Product> result) {
                if(result.size() != 0) {
                    mProductAdapter.addData(result);
                }
            }
            @Override
            public void onFailure(@NonNull Exception exp) {
                setError();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadMoreProduct(View view) {
        getProducts();
    }


    public void actionDirectToStore(View view) {
        //open google map (can be app or browser)
        Uri url = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + store.getGeo());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    public void actionSaveStore(View view) {
        Toast.makeText(this, "Save Store", Toast.LENGTH_SHORT).show();
    }

    public void actionCommentToStore(View view) {
        Toast.makeText(this, "Comment store on new fragment", Toast.LENGTH_SHORT).show();
    }

    public void actionShareStore(View view) {
        Uri imageUri = Uri.parse("android.resource://" + getPackageName()
                + "/mipmap/" + "minh_test_upload");
        String str_mess = store_detail_name.getText() + "\n" + store_detail_address.getText() + ".";
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, str_mess);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.store_detail_product_share_mess)));
    }

    public void actionShowMap(View view) {

    }

    public void loadMoreUtility(View view) {
        List<Store.Utility> utilities = store.getUtilities();
        if(utilities.size() == 0) {
            return;
        }
        if (mDialogUtility == null) {
            mDialogUtility = new Dialog(this);
        }
        mDialogUtility.setContentView(R.layout.minh_store_detail_dialog_utility);
        ListView list_utility = mDialogUtility.findViewById(R.id.store_detail_list_utility);
        ArrayAdapter<Store.Utility> arrayAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, utilities);
        list_utility.setAdapter(arrayAdapter);
        Button btn_close_dialog_product = mDialogUtility.findViewById(R.id.store_detail_btn_close_utility);
        btn_close_dialog_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogUtility.dismiss();
            }
        });
        mDialogUtility.show();
    }

    public void loadMoreComments(View view) {

    }

    public void actionContactToStore(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                store.getContact(), null)));
    }
}
