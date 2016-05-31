package me.calebjones.spacelaunchnow.content.models.realm;

import io.realm.RealmObject;

public class RealmStr extends RealmObject {
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
