package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.RocketFamily;

public class RocketFamilyResponse extends BaseResponse {
    private RocketFamily[] RocketFamilies;

    public RocketFamily[] getRocketFamilies() {
        return RocketFamilies;
    }
}
