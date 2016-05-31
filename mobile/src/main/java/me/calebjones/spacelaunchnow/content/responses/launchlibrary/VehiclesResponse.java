package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.RocketRealm;

public class VehiclesResponse extends BaseResponse {
    private RocketRealm[] rockets;

    public RocketRealm[] getRockets() {
        return rockets;
    }
}
