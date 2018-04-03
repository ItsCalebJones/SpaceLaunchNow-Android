package me.calebjones.spacelaunchnow.content.models;

import org.simpleframework.xml.Attribute;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Media extends RealmObject {

        @Attribute(name = "url")
        private String url;

        @Attribute(name = "type")
        private String type;
}
