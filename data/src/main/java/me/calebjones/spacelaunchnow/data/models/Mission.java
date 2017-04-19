
package me.calebjones.spacelaunchnow.data.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Mission extends RealmObject {

    @PrimaryKey
    private Integer id;
    private Integer type;
    private String name;
    private String description;
    private String infoURL;
    private String wikiURL;
    private String typeName;
    private LaunchMini launch;

    public Integer getId() {
        return id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LaunchMini getLaunch() {
        return launch;
    }

    public void setLaunch(LaunchMini launch) {
        this.launch = launch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public String getWikiURL() {
        return wikiURL;
    }

    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    public String getTypeName(){return typeName;}

    public void setTypeName(String typeName){this.typeName = typeName;}

}
