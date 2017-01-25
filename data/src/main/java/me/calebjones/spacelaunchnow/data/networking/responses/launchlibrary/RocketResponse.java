package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.RocketRealm;

public class RocketResponse extends BaseResponse {
    private RocketRealm[] rockets;

    public RocketRealm[] getRockets() {
        return rockets;
    }
}
