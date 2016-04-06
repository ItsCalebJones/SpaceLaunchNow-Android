
package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pad implements Serializable {

    private Integer id;
    private String name;
    private String infoURL;
    private String wikiURL;
    private String mapURL;
    private Double latitude;
    private Double longitude;
    private List<LocationAgency> agencies = new ArrayList<LocationAgency>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public String getMapURL() {
        return mapURL;
    }

    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<LocationAgency> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<LocationAgency> agencies) {
        this.agencies = agencies;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
