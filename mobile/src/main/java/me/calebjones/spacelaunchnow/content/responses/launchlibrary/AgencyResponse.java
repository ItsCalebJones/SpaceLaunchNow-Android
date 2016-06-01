package me.calebjones.spacelaunchnow.content.responses.launchlibrary;

import me.calebjones.spacelaunchnow.content.models.realm.AgencyRealm;

public class AgencyResponse extends BaseResponse {
    private AgencyRealm[] agencies;

    public AgencyRealm[] getAgencies() {
        return agencies;
    }
}
