package org.nam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.R;
import org.nam.firebase.IResult;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;

public class MyMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private Context context;
    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        LatLng[] latLngs = MathUtils.getBoxPoints(map.getCameraPosition().target, 2);
        map.addPolygon(new PolygonOptions().add(latLngs).fillColor(0xff000000));
        getLastLocation(new IResult<Location>() {
            @Override
            public void onResult(Location result) {
                LatLng location = new LatLng(result.getLatitude(), result.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
            @Override
            public void onFailure(@NonNull Exception exp) {
            }
        });
    }

    private void getLastLocation(final IResult<Location> result) {
        LocationUtils.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location == null) {
                            return;
                        }
                        result.onResult(location);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
        });
    }

}
