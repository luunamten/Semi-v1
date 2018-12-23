package org.nam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.nam.R;
import org.nam.contract.Contract;
import org.nam.firebase.IResult;
import org.nam.firebase.StoreConnector;
import org.nam.object.IHaveIdAndName;
import org.nam.object.Location;
import org.nam.object.Store;
import org.nam.util.LocationUtils;
import org.nam.util.MathUtils;
import org.nam.util.SearchBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyMapFragment extends Fragment implements OnMapReadyCallback, ISearch, View.OnClickListener {

    private GoogleMap map;
    private int type;
    private String query;
    private SearchBox searchBox;
    private StoreConnector storeConnector;
    private List<Marker> markers;
    private AppCompatImageView flagImageView;
    private int scaleFactor;
    private long latestCallId;
    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IUseFragment) {
            ((IUseFragment)context).onFragmentAttached(this);
        }
        storeConnector = StoreConnector.getInstance();
        scaleFactor = 0;
        type = -1;
        query = "";
        markers = new ArrayList<>();
        latestCallId = 0L;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        flagImageView = view.findViewById(R.id.flagImageView);
        AppCompatImageButton upButton = view.findViewById(R.id.upButton);
        AppCompatImageButton downButton = view.findViewById(R.id.downButton);
        mapFragment.getMapAsync(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        return view;
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
                flagImageView.setVisibility(View.INVISIBLE);
                if(!searchBox.isContains(cameraTarget)) {
                    searchBox.setCenter(cameraTarget);
                    searchBox.draw(map);
                    searchNearbyCenterStores(true);
                } else {
                    searchNearbyCenterStores(false);
                }
            }
        });
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                flagImageView.setVisibility(View.VISIBLE);
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
                    public void onSuccess(@Nullable android.location.Location location) {
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
        searchNearbyCenterStores(true);
    }

    private void searchNearbyCenterStores(final boolean isGetNew) {
        if(isGetNew) {
            removePlaces();
        }
        final long currentCallId = ++latestCallId;
        storeConnector.getNearbyStoresByKeywords(searchBox.getLocationCenter(), markers.size(),
                type, query, searchBox.getDimen(), new IResult<List<Store>>() {
                    @Override
                    public void onResult(@NonNull List<Store> result) {
                        Log.w("test_order", String.valueOf(currentCallId));
                        if(currentCallId != latestCallId || result.size() == 0) {
                            return;
                        }
                        addPlaces(result);
                    }
                    @Override
                    public void onFailure(@NonNull Exception exp) { }
                });
    }

    private void addPlaces(List<Store> stores) {
        for(Store store : stores) {
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.place))
                    .title(store.getTitle())
                    .anchor(0.5f,0.5f) //center icon
                    .position(new LatLng(store.getGeo().getLatitude(),
                            store.getGeo().getLongitude()));
            Marker marker = map.addMarker(options);
            markers.add(marker);
        }
    }

    private void removePlaces() {
        for(Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    @Override
    public void scroll(String lastId) { }

    @Override
    public void clickItem(String id) { }

    private void updateWhenScaleSearchBox() {
        searchBox.setDimen(Contract.VISIBLE_BOX_MIN_DIMEN * (1 << scaleFactor));
        moveCameraToSearchBox(searchBox.getCenter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upButton:
                if(scaleFactor == Contract.DIMEN_SCALE_FACTOR_MAX) {
                    return;
                }
                scaleFactor++;
                updateWhenScaleSearchBox();
                break;
            case R.id.downButton:
                if(scaleFactor == 0) {
                    return;
                }
                scaleFactor--;
                removePlaces();
                updateWhenScaleSearchBox();
                break;
        }
    }
}
