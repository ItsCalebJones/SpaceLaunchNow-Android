package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class LaunchResponse extends BaseResponse {
    @SerializedName("results")
    private List<Launch> launches;

    public List<Launch> getLaunches() {
        return launches;
    }
}
