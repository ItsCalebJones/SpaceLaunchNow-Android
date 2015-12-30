package me.calebjones.spacelaunchnow.utils;

import com.google.android.gms.maps.model.LatLng;

public class MapLocation {
    public String name;
    public LatLng center;

    @SuppressWarnings("unused")
    public MapLocation() {}

    public MapLocation(double lat, double lng) {
        this.center = new LatLng(lat, lng);
    }
}