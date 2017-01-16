package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.LaunchRealm;

public class LaunchResponse extends BaseResponse {
    private LaunchRealm[] launches;

    public LaunchRealm[] getLaunches() {
        return launches;
    }
}
