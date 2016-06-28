package me.calebjones.spacelaunchnow.content.responses.base;

import me.calebjones.spacelaunchnow.content.models.natives.Launcher;
import me.calebjones.spacelaunchnow.content.models.realm.RocketDetailsRealm;

public class VehicleResponse {
    private RocketDetailsRealm[] vehicles;

    public RocketDetailsRealm[] getVehicles() {
        return vehicles;
    }
}
