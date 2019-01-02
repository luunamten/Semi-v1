package org.nam.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.nam.MyApp;
import org.nam.R;
import org.nam.custom.InfoDialog;

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

    public static void showInfoDialog(Activity activity, String message) {
        new InfoDialog(activity, message).show();
    }
}
