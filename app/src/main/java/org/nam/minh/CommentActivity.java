package org.nam.minh;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.CommentConnector;
import org.nam.firebase.IResult;
import org.nam.minh.object.Product;
import org.nam.object.Store;

public class CommentActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private TextView storeNameTextView;
    private TextView storeAddressTextView;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_comment);
        getStoreFromIntent();
        setupToolbar();
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.comment_content_input);
        storeNameTextView = findViewById(R.id.store_detail_name);
        storeAddressTextView = findViewById(R.id.store_detail_address);
    }
    private void setupToolbar(){
        Toolbar toolbar_store_detail = findViewById(R.id.toolbar_store_detail);
        toolbar_store_detail.setTitle("");
        setSupportActionBar(toolbar_store_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getStoreFromIntent() {
        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra(Contract.BUNDLE_STORE_KEY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onCancelled(View view) {
        finish();
    }

    public void onComment(View view){
        float rating = ratingBar.getRating();
        String comment = commentEditText.getText().toString();
        CommentConnector.getInstance().postComment(store.getId(), comment, rating, new IResult<Object>() {
            @Override
            public void onResult(Object result) {

            }

            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });
    }
}
