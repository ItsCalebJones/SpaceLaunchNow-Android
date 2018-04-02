package me.calebjones.spacelaunchnow.content.models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;

@Root(strict = false)
public class Channel extends RealmObject {

    @Element(name = "title")
    public String title;

    @ElementList(inline = true, name="item")
    public RealmList<Article> articles;

    @LinkingObjects("channel")
    final RealmResults<NewsFeedResponse> newsFeedResponse = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Article> getArticles() {
        return articles;
    }

    public void setArticles(RealmList<Article> articles) {
        this.articles = articles;
    }
}
