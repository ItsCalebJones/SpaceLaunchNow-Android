package me.calebjones.spacelaunchnow.data.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RocketFamilyRealm extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private String infoURL;
    private String wikiURL;
    private String countrycode;
    private RealmList<RealmStr> infoURLs;

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

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public RealmList<RealmStr> getInfoURLs() {
        return infoURLs;
    }

    public void setInfoURLs(RealmList<RealmStr> infoURLs) {
        this.infoURLs = infoURLs;
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

}
