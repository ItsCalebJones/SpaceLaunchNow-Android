package me.calebjones.spacelaunchnow.content.responses;

import java.util.List;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;

public class LaunchResponse {
    private LaunchRealm[] launches;

    public LaunchRealm[] getLaunches() {
        return launches;
    }
}
