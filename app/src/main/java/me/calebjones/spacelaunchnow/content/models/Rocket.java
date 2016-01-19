
package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rocket implements Serializable {

    private Integer id;
    private String name;
    private String configuration;
    private String familyname;
    private String infoURL;
    private String wikiURL;
    private String imageURL;
    private Family family;
    private List<RocketAgency> agencies = new ArrayList<RocketAgency>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The configuration
     */
    public String getInfoURL() {
        return infoURL;
    }

    /**
     * 
     * @param configuration
     *     The configuration
     */
    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    /**
     *
     * @return
     *     The configuration
     */
    public String getWikiURL() {
        return wikiURL;
    }

    /**
     *
     * @param configuration
     *     The configuration
     */
    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    /**
     *
     * @return
     *     The configuration
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     *
     * @param configuration
     *     The configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    /**
     * 
     * @return
     *     The familyname
     */
    public Family getFamily() {
        return family;
    }

    /**
     * 
     * @param familyname
     *     The familyname
     */
    public void setFamily(Family family) {
        this.family = family;
    }

    /**
     *
     * @return
     *     The familyname
     */
    public String getFamilyname() {
        return familyname;
    }

    /**
     *
     * @param familyname
     *     The familyname
     */
    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    /**
     *
     * @return
     *     The familyname
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     *
     * @param familyname
     *     The familyname
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * 
     * @return
     *     The agencies
     */
    public List<RocketAgency> getAgencies() {
        return agencies;
    }

    /**
     * 
     * @param agencies
     *     The agencies
     */
    public void setAgencies(List<RocketAgency> agencies) {
        this.agencies = agencies;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
