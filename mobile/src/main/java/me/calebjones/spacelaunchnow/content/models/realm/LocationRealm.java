
package me.calebjones.spacelaunchnow.content.models.realm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.content.models.Pad;

public class LocationRealm extends RealmObject {

    private Integer id;
    private String name;
    private RealmList<PadRealm> pads = new RealmList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<PadRealm> getPads() {
        return pads;
    }

    public void setPads(RealmList<PadRealm> pads) {
        this.pads = pads;
    }

}
