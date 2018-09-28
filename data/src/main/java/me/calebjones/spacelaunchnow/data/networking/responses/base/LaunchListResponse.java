package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;

public class LaunchListResponse extends BaseResponse {
    @SerializedName("results")
    private List<LaunchList> launches;

    public List<LaunchList> getLaunches() {
        return launches;
    }
}
