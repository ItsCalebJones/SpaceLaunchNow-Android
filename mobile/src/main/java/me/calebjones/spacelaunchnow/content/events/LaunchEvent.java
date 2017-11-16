package me.calebjones.spacelaunchnow.content.events;

import me.calebjones.spacelaunchnow.data.models.Launch;


public class LaunchEvent {

    public final Launch launch;

    public LaunchEvent(Launch launch) {
        this.launch = launch;
    }
}
