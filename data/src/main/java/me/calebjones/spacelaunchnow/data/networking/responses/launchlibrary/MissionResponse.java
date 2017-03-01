package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.Mission;

public class MissionResponse extends BaseResponse {
    private Mission[] missions;

    public Mission[] getMissions() {
        return missions;
    }
}
