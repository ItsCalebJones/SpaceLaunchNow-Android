package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.launchlibrary.Rocket;

public class RocketResponse extends BaseResponse {
    private Rocket[] rockets;

    public Rocket[] getRockets() {
        return rockets;
    }
}
