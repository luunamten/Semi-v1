package org.nam.custom;

import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.nam.MyApp;
import org.nam.R;
import org.nam.object.Store;
import org.nam.util.ObjectUtils;

import java.util.Map;

public class StoreInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {

        return null;
    }
}
