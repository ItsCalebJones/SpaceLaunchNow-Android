package me.calebjones.spacelaunchnow.content.models.realm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FamilyRealm extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private String agencies;


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


    public String getAgencies() {
        return agencies;
    }


    public void setAgencies(String agencies) {
        this.agencies = agencies;
    }



}