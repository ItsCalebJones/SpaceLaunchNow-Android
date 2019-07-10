package me.calebjones.spacelaunchnow.data.models.main;

import io.realm.RealmObject;

public class CalendarEvent extends RealmObject {

    private Long id;
    private String launchId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLaunchId() {
        return launchId;
    }

    public void setLaunchId(String launch_id) {
        this.launchId = launch_id;
    }
}
