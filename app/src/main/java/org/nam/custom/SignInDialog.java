package org.nam.custom;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import org.nam.R;

public class SignInDialog extends Dialog implements View.OnClickListener {

    public SignInDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_sign_in);
        final SignInButton signInButton = findViewById(R.id.signInButton);
        final AppCompatButton closeButton = findViewById(R.id.closeButton);
        signInButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                break;
            case R.id.closeButton:
                dismiss();
                break;
        }
    }
}
