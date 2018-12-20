package org.nam.util;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

//Box used for draw on GoogleMap and check range.
public class SearchBox {
    public static final double DEFAULT_DIMEN = 0.25;
    public static final int BOX_STROKE_COLOR = 0x400000ff;
    private double dimen;
    private LatLng[] points;

    public SearchBox() { }

    public SearchBox(@NonNull Location location, double dimen) {
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(location, dimen);
    }
    public SearchBox(@NonNull LatLng center, double dimen) {
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(center, dimen);
    }

    public SearchBox(@NonNull Location location) {
        this.dimen = DEFAULT_DIMEN;
        points = MathUtils.getBoxPoints(location, DEFAULT_DIMEN);
    }
    public SearchBox(@NonNull LatLng center) {
        this.dimen = DEFAULT_DIMEN;
        points = MathUtils.getBoxPoints(center, DEFAULT_DIMEN);
    }

    public boolean isContains(LatLng point) {
        if(point.longitude > points[0].longitude && point.longitude < points[2].longitude &&
                point.latitude < points[0].latitude && point.latitude > points[2].latitude) {
            return true;
        }
        return false;
    }

    public boolean isContains(Location location) {
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        return isContains(point);
    }

    public LatLng[] getPoints() {
        return points;
    }

    public void setPoints(LatLng center, double dimen) {
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(center, dimen);
    }

    public void setPoints(Location location, double dimen) {
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(location, dimen);
    }

    public void setPoints(LatLng center) {
        this.dimen = dimen;
        points = MathUtils.getBoxPoints(center, DEFAULT_DIMEN);
    }

    public void setPoints(Location location) {
        this.dimen = DEFAULT_DIMEN;
        points = MathUtils.getBoxPoints(location, DEFAULT_DIMEN);
    }

    public LatLng getTopLeft() {
        return points[0];
    }

    public LatLng getBottomRight() {
        return points[2];
    }

    public double getDimen() {
        return dimen;
    }
}
