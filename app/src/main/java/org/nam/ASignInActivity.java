package org.nam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ASignInActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asign_in);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            openMapActivity();
            finish();
        }
    }

    public void onSignInButtonClick(View view) {
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ASignInActivity.this, task.getResult().getUser().getDisplayName()
                            , Toast.LENGTH_SHORT).show();
                    openMapActivity();
                    finish();
                } else {
                    Toast.makeText(ASignInActivity.this, "Sign In thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openMapActivity() {
        Intent openMapIntent = new Intent(this, MapsActivity.class);
        startActivity(openMapIntent);
    }
}
