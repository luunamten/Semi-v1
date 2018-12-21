package org.nam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.IResult;
import org.nam.firebase.StoreConnector;
import org.nam.object.Location;
import org.nam.object.Store;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;
import org.nam.util.SearchBox;

import java.util.Arrays;
import java.util.List;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, ISearch, View.OnClickListener {

    private GoogleMap map;
    private int type;
    private String query;
    private SearchBox searchBox;
    private StoreConnector storeConnector;
    private int scaleFactor;
    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        AppCompatImageButton upButton = view.findViewById(R.id.upButton);
        AppCompatImageButton downButton = view.findViewById(R.id.downButton);
        mapFragment.getMapAsync(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        storeConnector = StoreConnector.getInstance();
        scaleFactor = 0;
        type = -1;
        query = "";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        searchBox = new SearchBox();
        searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(false);
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng cameraTarget = map.getCameraPosition().target;
                if(!searchBox.isContains(cameraTarget)) {
                    searchBox.setCenter(cameraTarget);
                    searchBox.draw(map);
                } else {

                }
            }
        });
        getLastLocation(new IResult<android.location.Location>() {
            @Override
            public void onResult(android.location.Location result) {
                final LatLng location = new LatLng(result.getLatitude(), result.getLongitude());
                moveCameraToSearchBox(location);
            }
            @Override
            public void onFailure(@NonNull Exception exp) { }
        });
    }

    private void getLastLocation(final IResult<android.location.Location> result) {
        LocationUtils.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
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

    private void moveCameraToSearchBox(LatLng location) {
        searchBox.setCenter(location);
        searchBox.draw(map);
        LatLngBounds bounds = new LatLngBounds( searchBox.getSouthWest(),
                searchBox.getNorthEast());
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Contract.VISIBLE_BOX_PADDING));
    }

    @Override
    public void search(int type, String query) {
        this.type = type;
        this.query = query;
    }

    private void searchNearbyCenterStores() {
        storeConnector.getNearbyStoresByKeywords(searchBox.getLocationCenter(), 0,
                type, query, searchBox.getDimen(), new IResult<List<Store>>() {
                    @Override
                    public void onResult(List<Store> result) {

                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) { }
                });
    }

    @Override
    public void scroll(String lastId) { }

    @Override
    public void clickItem(String id) { }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upButton:
                if(scaleFactor == Contract.DIMEN_SCALE_FACTOR_MAX) {
                    return;
                }
                scaleFactor++;
                searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN * (1 << scaleFactor));
                moveCameraToSearchBox(map.getCameraPosition().target);
                break;
            case R.id.downButton:
                if(scaleFactor == 0) {
                    return;
                }
                scaleFactor--;
                searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN * (1 << scaleFactor));
                moveCameraToSearchBox(map.getCameraPosition().target);
                break;
        }
    }
}
