package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.LocationRealm;

public class LocationResponse extends BaseResponse{
    private LocationRealm[] locations;

    public LocationRealm[] getLocations() {
        return locations;
    }
}
