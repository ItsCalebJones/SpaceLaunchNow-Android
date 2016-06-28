package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;

public class LaunchResponse extends BaseResponse {
    private LaunchRealm[] launches;

    public LaunchRealm[] getLaunches() {
        return launches;
    }
}
