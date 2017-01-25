package me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary;

import me.calebjones.spacelaunchnow.data.models.realm.AgencyRealm;

public class AgencyResponse extends BaseResponse {
    private AgencyRealm[] agencies;

    public AgencyRealm[] getAgencies() {
        return agencies;
    }
}
