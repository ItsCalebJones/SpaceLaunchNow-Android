package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;

public class LaunchResponse extends BaseResponse {

    private List<Launch> launches;

    public List<Launch> getLaunches() {
        return launches;
    }
}
