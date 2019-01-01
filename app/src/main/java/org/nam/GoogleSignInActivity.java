package org.nam;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleSignInActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        GoogleSignInOptions opts = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();
        client = GoogleSignIn.getClient(this, opts);
        SignInButton button = findViewById(R.id.gg_sign_in_but);
        button.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Task<GoogleSignInAccount> task = client.silentSignIn();
        if(task.isSuccessful()) {
            Log.w("idtoken", task.getResult().getIdToken());
            Log.w("yourid", task.getResult().getId());
        } else {
            task.addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if(task.isSuccessful()) {
                        Log.w("idtoken", task.getResult().getIdToken());
                        Log.w("yourid", task.getResult().getId());
                    } else {
                        Toast.makeText(GoogleSignInActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gg_sign_in_but:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, 4);
    }


    @Override
    public void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if(request == 4) {
            //if(result == Activity.RESULT_OK) {
                    /*GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                            } catch (ApiException exp) {
                                String fuck = "";
                                switch (exp.getStatusCode()) {
                                    case GoogleSignInStatusCodes.INVALID_ACCOUNT:
                                        fuck = "invalid acc";
                                        break;
                                    case GoogleSignInStatusCodes.DEVELOPER_ERROR:
                                        fuck = "dev";
                                        break;
                                    case GoogleSignInStatusCodes.RESOLUTION_REQUIRED:
                                        fuck = "required";
                                        break;
                                    case GoogleSignInStatusCodes.INTERNAL_ERROR:
                                        fuck = "interal";
                                        break;
                                    case GoogleSignInStatusCodes.API_NOT_CONNECTED:
                                        fuck = "api";
                                        break;
                                    case GoogleSignInStatusCodes.CANCELED:
                                        fuck = "interal";
                                        break;
                                    case GoogleSignInStatusCodes.ERROR:
                                        fuck = "error";
                                        break;
                                    case GoogleSignInStatusCodes.INTERRUPTED:
                                        fuck = "interrupted";
                                        break;
                                    case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                                        fuck = "sign in failed";
                                        break;
                                    case GoogleSignInStatusCodes.SIGN_IN_REQUIRED:
                                        fuck = "sign in required";
                                        break;
                                    case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                                        fuck = "sign in cancelled";
                                    case GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS:
                                        fuck = "progress";

                                }
                                Toast.makeText(GoogleSignInActivity.this, fuck, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    /*ResolvableApiException rexp = (ResolvableApiException) exp;
                    try {
                        rexp.startResolutionForResult(this, 4);
                    } catch (IntentSender.SendIntentException e) {
                        Toast.makeText(this, "Fail part2", Toast.LENGTH_SHORT).show();
                    }*/

            //}
        }
    }
}
