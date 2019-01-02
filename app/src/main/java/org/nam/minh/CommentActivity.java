package org.nam.minh;

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
import org.nam.minh.object.Product;

public class CommentActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private TextView storeNameTextView;
    private TextView storeAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_comment);
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

    }
}
