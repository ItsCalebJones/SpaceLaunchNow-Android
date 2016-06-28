package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.RocketFamilyRealm;

public class RocketFamilyResponse extends BaseResponse {
    private RocketFamilyRealm[] RocketFamilies;

    public RocketFamilyRealm[] getRocketFamilies() {
        return RocketFamilies;
    }
}
