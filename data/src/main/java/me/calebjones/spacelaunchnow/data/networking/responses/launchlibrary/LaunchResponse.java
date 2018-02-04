package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;

public class LaunchResponse extends BaseResponse {

    private Launch[] launches;

    public Launch[] getLaunches() {
        return launches;
    }
}
