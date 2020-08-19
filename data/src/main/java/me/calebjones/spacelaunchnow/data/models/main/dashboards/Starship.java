package me.calebjones.spacelaunchnow.data.models.main.dashboards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.VidURL;
import me.calebjones.spacelaunchnow.data.models.main.launcher.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.starship.Notice;
import me.calebjones.spacelaunchnow.data.models.main.starship.RoadClosure;

public class Starship extends RealmObject {

    @SerializedName("launches")
    @Expose
    public RealmList<Launch> launches;
    @SerializedName("events")
    @Expose
    public RealmList<Event> events;
    @SerializedName("live_streams")
    @Expose
    public RealmList<VidURL> liveStreams;
    @SerializedName("road_closures")
    @Expose
    public RealmList<RoadClosure> roadClosures;
    @SerializedName("notices")
    @Expose
    public RealmList<Notice> notices;
    @SerializedName("vehicles")
    @Expose
    public RealmList<Launcher> vehicles;

    public RealmList<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(RealmList<Launch> launches) {
        this.launches = launches;
    }

    public RealmList<Event> getEvents() {
        return events;
    }

    public void setEvents(RealmList<Event> events) {
        this.events = events;
    }

    public RealmList<VidURL> getLiveStreams() {
        return liveStreams;
    }

    public void setLiveStreams(RealmList<VidURL> liveStreams) {
        this.liveStreams = liveStreams;
    }

    public RealmList<RoadClosure> getRoadClosures() {
        return roadClosures;
    }

    public void setRoadClosures(RealmList<RoadClosure> roadClosures) {
        this.roadClosures = roadClosures;
    }

    public RealmList<Notice> getNotices() {
        return notices;
    }

    public void setNotices(RealmList<Notice> notices) {
        this.notices = notices;
    }

    public RealmList<Launcher> getVehicles() {
        return vehicles;
    }

    public void setVehicles(RealmList<Launcher> vehicles) {
        this.vehicles = vehicles;
    }
}
