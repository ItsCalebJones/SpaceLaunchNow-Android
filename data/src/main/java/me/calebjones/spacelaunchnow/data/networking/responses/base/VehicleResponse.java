package me.calebjones.spacelaunchnow.data.networking.responses.base;

import me.calebjones.spacelaunchnow.data.models.realm.RocketDetails;

public class VehicleResponse {
    private RocketDetails[] vehicles;

    public RocketDetails[] getVehicles() {
        return vehicles;
    }
}
