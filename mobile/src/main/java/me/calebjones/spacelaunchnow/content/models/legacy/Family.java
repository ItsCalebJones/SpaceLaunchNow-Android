package me.calebjones.spacelaunchnow.content.models.legacy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Family implements Serializable {

    private Integer id;
    private String name;
    private String agencies;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The agencies
     */
    public String getAgencies() {
        return agencies;
    }

    /**
     *
     * @param agencies
     * The agencies
     */
    public void setAgencies(String agencies) {
        this.agencies = agencies;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}