package me.calebjones.spacelaunchnow.data.models.main.dashboards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;

public class PreviousObjects extends RealmObject {

    @SerializedName("launches")
    @Expose
    public RealmList<LaunchList> launches;
    @SerializedName("events")
    @Expose
    public RealmList<Event> events;

    public RealmList<LaunchList> getLaunches() {
        return launches;
    }

    public void setLaunches(RealmList<LaunchList> launches) {
        this.launches = launches;
    }

    public RealmList<Event> getEvents() {
        return events;
    }

    public void setEvents(RealmList<Event> events) {
        this.events = events;
    }
}
