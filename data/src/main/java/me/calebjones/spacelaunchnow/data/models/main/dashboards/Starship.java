package me.calebjones.spacelaunchnow.data.models.main.dashboards;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.data.models.main.Update;
import me.calebjones.spacelaunchnow.data.models.main.VidURL;
import me.calebjones.spacelaunchnow.data.models.main.launcher.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.starship.Notice;
import me.calebjones.spacelaunchnow.data.models.main.starship.RoadClosure;

public class Starship extends RealmObject {

    @SerializedName("previous")
    @Expose
    public PreviousObjects previousObjects;
    @SerializedName("upcoming")
    @Expose
    public UpcomingObjects upcomingObjects;
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
    @SerializedName("updates")
    @Expose
    public RealmList<Update> updates = null;


    public PreviousObjects getPreviousObjects() {
        return previousObjects;
    }

    public void setPreviousObjects(PreviousObjects previousObjects) {
        this.previousObjects = previousObjects;
    }

    public UpcomingObjects getUpcomingObjects() {
        return upcomingObjects;
    }

    public void setUpcomingObjects(UpcomingObjects upcomingObjects) {
        this.upcomingObjects = upcomingObjects;
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

    public RealmList<Update> getUpdates() {
        return updates;
    }

    public void setUpdates(RealmList<Update> updates) {
        this.updates = updates;
    }
}
