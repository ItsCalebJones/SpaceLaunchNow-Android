package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.LaunchWear;

public class LaunchWearResponse extends BaseResponse {

    private LaunchWear[] launches;

    public LaunchWear[] getLaunches() {
        return launches;
    }
}
