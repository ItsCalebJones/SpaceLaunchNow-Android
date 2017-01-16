
package me.calebjones.spacelaunchnow.data.models.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LaunchRealmMini extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private Date windowstart;
    private Date windowend;
    private Date net;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getWindowstart() {
        return windowstart;
    }

    public void setWindowstart(Date windowstart) {
        this.windowstart = windowstart;
    }

    public Date getWindowend() {
        return windowend;
    }

    public void setWindowend(Date windowend) {
        this.windowend = windowend;
    }

    public Date getNet() {
        return net;
    }

    public void setNet(Date net) {
        this.net = net;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
