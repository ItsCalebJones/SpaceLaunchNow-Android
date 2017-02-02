package me.calebjones.spacelaunchnow.data.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Family extends RealmObject {

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
