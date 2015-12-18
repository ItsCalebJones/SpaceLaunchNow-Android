
package calebjones.me.spacelaunchnow.content.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rocket {

    private Integer id;
    private String name;
    private String configuration;
    private String familyname;
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
