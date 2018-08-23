package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.main.Launch;
public class LaunchWearResponse extends BaseResponse {

    private Launch[] launches;

    public Launch[] getLaunches() {
        return launches;
    }
}
