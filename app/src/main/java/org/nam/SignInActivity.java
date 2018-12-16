package org.nam;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.SignInHubActivity;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;


public class SignInActivity extends AppCompatActivity {

    private final static int SIGN_IN_CODE = 0;
    private FirebaseAuth auth;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkFirebaseUser();
    }

    public void onSignInButtonClick(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTosAndPrivacyPolicyUrls("", getString(R.string.privacy_policy_url))
                        .build(),
                SIGN_IN_CODE);
    }

    public void onSignOutButtonClick(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(response != null && response.isSuccessful()) {
                if (resultCode == Activity.RESULT_OK) {
                    openMapsActivity();
                } else {
                    Toast.makeText(this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private GoogleSignInAccount checkGoogleAccount() {
        GoogleSignInOptions opts = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.web_client_id))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, opts);
        Task<GoogleSignInAccount> task = client.silentSignIn();
        if(task.isSuccessful()) {
            return task.getResult();
        }
        return null;
    }

    private void checkFirebaseUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            user.getIdToken(true).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if(task.isSuccessful()) {
                        Log.w(    "firebaset", task.getResult().getToken());
                        Log.w("firebaseid", user.getUid());
                    } else {
                        Toast.makeText(SignInActivity.this, "ID Token Fail!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "user null!",
                    Toast.LENGTH_SHORT).show();
            AuthUI.getInstance().signOut(this);
        }
        /*AuthUI.getInstance().silentSignIn(this, providers)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, task.getResult().getUser().getDisplayName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignInActivity.this, "Failt", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void checkUser(final FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(this, new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    openMapsActivity();
                    finish();
                }
            }
        });
    }

    private void openMapsActivity() {
        Intent toMapIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(toMapIntent);
        finish();
    }
}
