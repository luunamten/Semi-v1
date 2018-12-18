package org.nam.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.contract.Contract;
import org.nam.firebase.IResult;

public final class LocationUtils {

    private  LocationUtils() {}

    public static final int RESOLUTION_CODE = 0;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 1;

    public static void checkAndRequestLocationSettings(final Activity activity,
                                                    final IResult<LocationSettingsResponse> result) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Contract.LOCATION_INTERVAL)
                .setFastestInterval(Contract.LOCATION_FASTEST_INTERVAL);
        final LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(request);
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                result.onResult(locationSettingsResponse);
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                final ResolvableApiException rexp = (ResolvableApiException) e;
                try {
                    rexp.startResolutionForResult(activity, RESOLUTION_CODE);
                } catch (IntentSender.SendIntentException sexp) {
                    result.onFailure(sexp);
                    Log.w("my_error", sexp);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public static Task<Location> getLastLocation(Context context) {
        final Task<Location> task = LocationServices.getFusedLocationProviderClient(context)
                .getLastLocation();
        return task;
    }

    public static boolean checkAndRequestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int locationResult = ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }
}
