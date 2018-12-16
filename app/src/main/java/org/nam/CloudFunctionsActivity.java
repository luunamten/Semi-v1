package org.nam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.JsonObject;

import org.nam.util.DialogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudFunctionsActivity extends AppCompatActivity {

    private FirebaseFunctions functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_functions);
        functions = FirebaseFunctions.getInstance();
    }

    public void callFunction(View view) {
        Map<String, Object> data = new HashMap<>();
        data.put("centerLat", 10.849557);
        data.put("centerLng", 106.760627);
        //data.put("dimen", 0.5);
        //data.put("keywords", "Store4");
        data.put("from", 0);
        functions.getHttpsCallable("nearbyStores")
                .call(data).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                List<Object> d = (List<Object>)httpsCallableResult.getData();
                if(d == null) {
                    Toast.makeText(CloudFunctionsActivity.this, "null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(d != null && d.size() == 0) {
                    Toast.makeText(CloudFunctionsActivity.this, "0", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogUtils.showAlert(CloudFunctionsActivity.this, "IResult", "");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    DialogUtils.showAlert(CloudFunctionsActivity.this, "Error", ffe.getMessage());
                }
            }
        });
    }
}
