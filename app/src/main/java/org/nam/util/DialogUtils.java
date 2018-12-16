package org.nam.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.nam.MyApp;
import org.nam.R;

public final class DialogUtils {
    private DialogUtils() {
    }

    public static void showAlert(Context context, String title, String message) {
        AlertDialog dgBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.adg_position_button, null)
                .create();
        dgBuilder.show();
    }
}
