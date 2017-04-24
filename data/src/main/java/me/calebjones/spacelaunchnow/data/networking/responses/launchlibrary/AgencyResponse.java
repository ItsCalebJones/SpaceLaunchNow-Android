package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.Agency;

public class AgencyResponse extends BaseResponse {
    private Agency[] agencies;

    public Agency[] getAgencies() {
        return agencies;
    }
}
