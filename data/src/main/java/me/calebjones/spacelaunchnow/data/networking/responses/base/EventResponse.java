package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class EventResponse extends BaseResponse {
    @SerializedName("results")
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }
}
