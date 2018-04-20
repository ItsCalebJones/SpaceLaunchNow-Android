package me.calebjones.spacelaunchnow.data.models.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Root(name = "rss", strict = false)
public class NewsFeedResponse extends RealmObject {

    @Attribute(name = "version")
    private String version;

    @Element(name = "channel")
    public Channel channel;

    @PrimaryKey
    private long lastUpdate;

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
