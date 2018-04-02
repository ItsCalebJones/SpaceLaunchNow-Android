package me.calebjones.spacelaunchnow.content.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import io.realm.RealmObject;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

@Root(strict = false, name="item")
public class Article extends RealmObject {

    @PrimaryKey
    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(name = "description")
    private String description;

    @Element(name = "guid", required = false)
    private String guid;

    @Element(name = "pubDate", required = false)
    private String pubDate;

    @Element(name = "media", required = false)
    private Media media;

    @Element(name = "source", required = false)
    private Source source;

    @LinkingObjects("articles")
    private final Channel channel = null;

}
