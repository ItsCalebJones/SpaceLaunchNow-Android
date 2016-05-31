
package me.calebjones.spacelaunchnow.content.models.legacy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mission implements Serializable {

    private Integer id,type;
    private String name, description,infoURL, wikiURL, typeName;
    private Launch launch;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


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

    public Launch getLaunch() {
        return launch;
    }

    public void setLaunch(Launch launch) {
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
