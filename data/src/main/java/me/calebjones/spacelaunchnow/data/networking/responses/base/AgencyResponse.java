package me.calebjones.spacelaunchnow.data.networking.responses.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.calebjones.spacelaunchnow.data.models.main.Agency;

public class AgencyResponse extends BaseResponse {
    @SerializedName("results")
    private List<Agency> agencies;

    public List<Agency> getAgencies() {
        return agencies;
    }
}
