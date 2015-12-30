
package me.calebjones.spacelaunchnow.content.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Launch implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String windowstart;
    private String windowend;
    private String net;
    private Integer wsstamp;
    private Integer westamp;
    private Integer netstamp;
    private Integer status;
    private Integer inhold;
    private Integer tbdtime;
    private Location location;
    private Rocket rocket;
    private List<Mission> missions = new ArrayList<Mission>();
    private String vidURL;
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
     *     The windowstart
     */
    public String getWindowstart() {
        return windowstart;
    }

    /**
     * 
     * @param windowstart
     *     The windowstart
     */
    public void setWindowstart(String windowstart) {
        this.windowstart = windowstart;
    }

    /**
     * 
     * @return
     *     The windowend
     */
    public String getWindowend() {
        return windowend;
    }

    /**
     * 
     * @param windowend
     *     The windowend
     */
    public void setWindowend(String windowend) {
        this.windowend = windowend;
    }

    /**
     * 
     * @return
     *     The net
     */
    public String getNet() {
        return net;
    }

    /**
     * 
     * @param net
     *     The net
     */
    public void setNet(String net) {
        this.net = net;
    }

    /**
     * 
     * @return
     *     The wsstamp
     */
    public Integer getWsstamp() {
        return wsstamp;
    }

    /**
     * 
     * @param wsstamp
     *     The wsstamp
     */
    public void setWsstamp(Integer wsstamp) {
        this.wsstamp = wsstamp;
    }

    /**
     * 
     * @return
     *     The westamp
     */
    public Integer getWestamp() {
        return westamp;
    }

    /**
     * 
     * @param westamp
     *     The westamp
     */
    public void setWestamp(Integer westamp) {
        this.westamp = westamp;
    }

    /**
     * 
     * @return
     *     The netstamp
     */
    public Integer getNetstamp() {
        return netstamp;
    }

    /**
     * 
     * @param netstamp
     *     The netstamp
     */
    public void setNetstamp(Integer netstamp) {
        this.netstamp = netstamp;
    }

    /**
     * 
     * @return
     *     The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The inhold
     */
    public Integer getInhold() {
        return inhold;
    }

    /**
     * 
     * @param inhold
     *     The inhold
     */
    public void setInhold(Integer inhold) {
        this.inhold = inhold;
    }

    /**
     * 
     * @return
     *     The tbdtime
     */
    public Integer getTbdtime() {
        return tbdtime;
    }

    /**
     * 
     * @param tbdtime
     *     The tbdtime
     */
    public void setTbdtime(Integer tbdtime) {
        this.tbdtime = tbdtime;
    }

    /**
     * 
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The rocket
     */
    public Rocket getRocket() {
        return rocket;
    }

    /**
     * 
     * @param rocket
     *     The rocket
     */
    public void setRocket(Rocket rocket) {
        this.rocket = rocket;
    }

    /**
     * 
     * @return
     *     The missions
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * 
     * @param missions
     *     The missions
     */
    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    /**
     * 
     * @return
     *     The vidURL
     */
    public String getVidURL() {
        return vidURL;
    }

    /**
     * 
     * @param vidURL
     *     The vidURL
     */
    public void setVidURL(String vidURL) {
        this.vidURL = vidURL;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
