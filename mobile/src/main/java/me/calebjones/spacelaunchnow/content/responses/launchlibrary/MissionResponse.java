package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.MissionRealm;

public class MissionResponse extends BaseResponse {
    private MissionRealm[] missions;

    public MissionRealm[] getMissions() {
        return missions;
    }
}
