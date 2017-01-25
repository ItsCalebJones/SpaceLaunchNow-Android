package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.MissionRealm;

public class MissionResponse extends BaseResponse {
    private MissionRealm[] missions;

    public MissionRealm[] getMissions() {
        return missions;
    }
}
