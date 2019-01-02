package org.nam.minh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.nam.R;
import org.nam.minh.object.Product;

public class CommentActivity extends AppCompatActivity {

    public static final String CURRENT_ID = "current_id";
    public static final String CURRENT_NAME = "current_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minh_activity_comment);


        SharedPrefs.getInstance().put(CURRENT_ID, 123);
        SharedPrefs.getInstance().put(CURRENT_NAME, "Minh");

        Log.d("semi_test", SharedPrefs.getInstance().get(CURRENT_ID, Integer.class).toString());
        Log.d("semi_test", SharedPrefs.getInstance().get(CURRENT_NAME, String.class));
    }
    private void initView(){
        Toolbar toolbar_store_detail = findViewById(R.id.toolbar_store_detail);
        toolbar_store_detail.setTitle("");
        setSupportActionBar(toolbar_store_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
