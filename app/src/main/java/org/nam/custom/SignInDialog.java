package org.nam.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import org.nam.R;
import org.nam.util.SignInUtils;

public class SignInDialog extends Dialog implements View.OnClickListener {
    private Activity activity;

    public SignInDialog(@NonNull Activity activity) {
        super(activity);
        setContentView(R.layout.dialog_sign_in);
        final SignInButton signInButton = findViewById(R.id.signInButton);
        final AppCompatButton closeButton = findViewById(R.id.closeButton);
        signInButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                SignInUtils.signInWithGoogle(activity);
                dismiss();
                break;
            case R.id.closeButton:
                dismiss();
                break;
        }
    }
}
