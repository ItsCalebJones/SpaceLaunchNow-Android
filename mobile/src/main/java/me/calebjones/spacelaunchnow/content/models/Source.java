package me.calebjones.spacelaunchnow.content.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Source extends RealmObject {

    @PrimaryKey
    @Attribute(name = "url")
    private String url;

    @Text
    private String text;

}
