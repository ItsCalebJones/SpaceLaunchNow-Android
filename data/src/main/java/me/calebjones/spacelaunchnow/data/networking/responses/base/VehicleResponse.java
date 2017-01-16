package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.realm.RocketDetailsRealm;

public class VehicleResponse {
    private RocketDetailsRealm[] vehicles;

    public RocketDetailsRealm[] getVehicles() {
        return vehicles;
    }
}
