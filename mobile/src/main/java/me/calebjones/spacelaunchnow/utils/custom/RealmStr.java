package me.calebjones.spacelaunchnow.utils.custom;

import io.realm.RealmObject;

public class RealmStr extends RealmObject {
    private String val;

    public RealmStr() {
    }

    public RealmStr(String val) {
        this.val = val;
    }

}
