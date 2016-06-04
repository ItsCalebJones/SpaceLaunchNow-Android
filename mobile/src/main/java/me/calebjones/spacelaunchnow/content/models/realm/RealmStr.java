package me.calebjones.spacelaunchnow.content.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmStr extends RealmObject {
    @PrimaryKey
    private String val;

    public RealmStr() {
    }

    public RealmStr(String val) {
        this.val = val;
    }

    public String getVal(){
        return val;
    }

}
