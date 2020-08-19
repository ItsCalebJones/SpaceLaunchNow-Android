package me.calebjones.spacelaunchnow.data.models.main.starship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class RoadClosure extends RealmObject {

    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("status")
    @Expose
    public RoadClosureStatus status;
    @SerializedName("window_end")
    @Expose
    public Date windowEnd;
    @SerializedName("window_start")
    @Expose
    public Date windowStart;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RoadClosureStatus getStatus() {
        return status;
    }

    public void setStatus(RoadClosureStatus status) {
        this.status = status;
    }

    public Date getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Date windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Date getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Date windowStart) {
        this.windowStart = windowStart;
    }
}
