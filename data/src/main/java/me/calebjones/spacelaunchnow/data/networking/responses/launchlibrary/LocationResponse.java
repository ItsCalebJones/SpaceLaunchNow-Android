package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.Location;

public class LocationResponse extends BaseResponse{
    private Location[] locations;

    public Location[] getLocations() {
        return locations;
    }
}
