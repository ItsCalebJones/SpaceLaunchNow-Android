package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.RocketFamilyRealm;

public class RocketFamilyResponse extends BaseResponse {
    private RocketFamilyRealm[] RocketFamilies;

    public RocketFamilyRealm[] getRocketFamilies() {
        return RocketFamilies;
    }
}
