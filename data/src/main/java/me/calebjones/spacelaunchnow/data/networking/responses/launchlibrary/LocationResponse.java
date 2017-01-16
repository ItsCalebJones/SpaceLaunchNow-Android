package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.LocationRealm;

public class LocationResponse extends BaseResponse{
    private LocationRealm[] locations;

    public LocationRealm[] getLocations() {
        return locations;
    }
}
