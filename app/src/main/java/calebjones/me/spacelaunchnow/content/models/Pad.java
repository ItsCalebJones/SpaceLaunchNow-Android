
package calebjones.me.spacelaunchnow.content.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pad {

    private Integer id;
    private String name;
    private String infoURL;
    private String wikiURL;
    private String mapURL;
    private String latitude;
    private String longitude;
    private List<LocationAgency> agencies = new ArrayList<LocationAgency>();
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
     *     The infoURL
     */
    public String getInfoURL() {
        return infoURL;
    }

    /**
     * 
     * @param infoURL
     *     The infoURL
     */
    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    /**
     * 
     * @return
     *     The wikiURL
     */
    public String getWikiURL() {
        return wikiURL;
    }

    /**
     * 
     * @param wikiURL
     *     The wikiURL
     */
    public void setWikiURL(String wikiURL) {
        this.wikiURL = wikiURL;
    }

    /**
     * 
     * @return
     *     The mapURL
     */
    public String getMapURL() {
        return mapURL;
    }

    /**
     * 
     * @param mapURL
     *     The mapURL
     */
    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The agencies
     */
    public List<LocationAgency> getAgencies() {
        return agencies;
    }

    /**
     * 
     * @param agencies
     *     The agencies
     */
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
