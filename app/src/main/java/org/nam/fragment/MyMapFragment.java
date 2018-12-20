package org.nam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.IResult;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;

import java.util.Arrays;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, ISearch {

    private GoogleMap map;
    private Context context;
    private double currentBoxDimen;
    private int type;
    private String query;
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
        currentBoxDimen = Contract.VISIBLE_BOX_MIN_DIMEN;
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
        map.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
            }
        });
        getLastLocation(new IResult<Location>() {
            @Override
            public void onResult(Location result) {
                final LatLng location = new LatLng(result.getLatitude(), result.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
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

    private void drawBox() {
        final LatLng[] hidingBoxPoints = MathUtils.getBoxPoints(map.getCameraPosition().target, 10);
        final LatLng[] visibleBoxPoints = MathUtils.getBoxPoints(map.getCameraPosition().target, 2);
        map.addPolygon(new PolygonOptions().add(hidingBoxPoints)
                .addHole(Arrays.asList(visibleBoxPoints)).fillColor(0x55000000));
    }

    @Override
    public void search(int type, String query) {
        this.type = type;
        this.query = query;
    }

    @Override
    public void scroll(String lastId) { }

    @Override
    public void clickItem(String id) { }
}
