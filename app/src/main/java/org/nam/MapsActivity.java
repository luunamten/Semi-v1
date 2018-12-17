package org.nam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private boolean isLocPermissionGranted;
    private boolean isLocSettingsOK;
    private LocationRequest locRequest;
    private FusedLocationProviderClient locProvider;
    private LocationCallback locCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult result) {
            Location lastLocation = result.getLastLocation();
            if(lastLocation != null) {
                setUISettings();
                moveCameraToLocation(new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude()), 15);
                locProvider.removeLocationUpdates(this);
            }
        }
    };

    private static final int REQUEST_LOC_PERMISSION = 1;
    private static final int START_RESOLUTION = 2;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_maps);
        //Map Fragment
        SupportMapFragment fragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        locRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(2000)
                .setInterval(2000);
        locProvider = LocationServices.getFusedLocationProviderClient(this);
        //action bar
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Geocoder g = new Geocoder(this, new Locale("vi"));
        try {
            List<Address> list = g.getFromLocation(10.8497471,106.7608088, 1);
            Address addr = list.get(0);
            //String str = String.format("%s,%s,%s", addr.getAddressLine(0), addr.getCountryName());
            Toast.makeText(this, addr.getSubAdminArea(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*StoreConnector.getInstance().getStoresByKeywords(2, "quan", "", new IResult<List<Store>>() {
            @Override
            public void onResult(List<Store> result) {
                DialogUtils.showAlert(MapsActivity.this, "F", result.get(0).getTitle());
            }

            @Override
            public void onFailure(@NonNull Exception exp) {

            }
        });*/
        /*StoreConnector.getInstance().getStoreById("24aMXwR4I1YicUTYu2JH", new IResult<Store>() {
            @Override
            public void onResult(Store result) {
                DialogUtils.showAlert(MapsActivity.this, "F", result.getFullName());
            }

            @Override
            public void onFailure(@NonNull Exception exp) {

            }
        });*/
        /*StoreConnector.getInstance().getNearbyStores(10.8497471, 106.7608088, 0, -1, new IResult<List<Store>>() {
            @Override
            public void onResult(List<Store> result) {
                DialogUtils.showAlert(MapsActivity.this, "F", String.valueOf(result.size()));
            }

            @Override
            public void onFailure(@NonNull Exception exp) {

            }
        });*/
        /*StoreConnector.getInstance().getNearbyStoresByKeywords(10.8497471, 106.7608088, 0, 2, "", 5, new IResult<List<Store>>() {
            @Override
            public void onResult(List<Store> result) {
                DialogUtils.showAlert(MapsActivity.this, "F", result.get(0).getTitle());
            }

            @Override
            public void onFailure(@NonNull Exception exp) {

            }
        });*/
        /*ProductConnector.getInstance().getProductsOfStore("7BnsEbUYNqnQQr3f3WbX", "", new IResult<List<Product>>() {
            @Override
            public void onResult(List<Product> result) {
                DialogUtils.showAlert(MapsActivity.this, "F", String.valueOf(result.size()));
            }

            @Override
            public void onFailure(@NonNull Exception exp) { }
        });*/
        /*ProductConnector.getInstance().getProductsByKeywords(1, "banh", "", new IResult<List<Product>>() {
            @Override
            public void onResult(List<Product> result) {
                DialogUtils.showAlert(MapsActivity.this, "F", result.get(0).getTitle());
            }

            @Override
            public void onFailure(@NonNull Exception exp) {}
        });*/
        /*ProductConnector.getInstance().getProductById("EHBSAKFBwHTsXn3y97i1", new IResult<Product>() {
            @Override
            public void onResult(Product result) {
                DialogUtils.showAlert(MapsActivity.this, "F", result.getFullName());
            }

            @Override
            public void onFailure(@NonNull Exception exp) {

            }
        });*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        checkAndRequestLocPermission();

    }

    private void checkAndRequestLocPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionResult != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOC_PERMISSION);
                return;
            }
        }
        onPermissionOK();
    }

    @Override
    public void onRequestPermissionsResult(int result,
                                           String[] permissions,
                                           int[] results){
        if(result == REQUEST_LOC_PERMISSION) {
            if(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionOK();
            }
        }
    }

    private void onPermissionOK() {
        isLocPermissionGranted = true;
        checkAndRequestLocSettings();
    }

    private void checkAndRequestLocSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());
        task.addOnCompleteListener(this, new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    onSettingsOK();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            ResolvableApiException exp = (ResolvableApiException) e;
                            try {
                                exp.startResolutionForResult(MapsActivity.this, START_RESOLUTION);
                            } catch (IntentSender.SendIntentException sexp) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int request, int result, Intent intent) {
        if(result == RESULT_OK) {
            if(request == START_RESOLUTION) {
                onSettingsOK();
            }
        }
    }

    private void onSettingsOK() {
        isLocSettingsOK = true;
        requestLocation();

    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        if(isLocSettingsOK && isLocPermissionGranted) {
            locProvider.requestLocationUpdates(locRequest, locCallback, null);
        }
    }

    private void moveCameraToLocation(LatLng target, float zoom) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(target)
                .zoom(zoom).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    @SuppressLint("MissingPermission")
    private void setUISettings() {
        UiSettings settings = map.getUiSettings();
        settings.setMapToolbarEnabled(false);
        if(isLocPermissionGranted) {
            settings.setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        } else {
            settings.setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(false);
        }
    }

    public void onSignOutButtonClick(View view) {
        FirebaseAuth.getInstance().signOut();
        openASignInActivity();
        finish();
    }

    private void openASignInActivity() {
        Intent openASignInIntent = new Intent(this, ASignInActivity.class);
        startActivity(openASignInIntent);
    }
}