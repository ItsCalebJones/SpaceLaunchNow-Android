package me.calebjones.spacelaunchnow.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;

public class StringRealmListConverter implements JsonSerializer<RealmList<RealmStr>>,
        JsonDeserializer<RealmList<RealmStr>> {

    @Override
    public JsonElement serialize(RealmList<RealmStr> src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonArray ja = new JsonArray();
        for (RealmStr strings : src) {
            ja.add(context.serialize(strings));
        }
        return ja;
    }

    @Override
    public RealmList<RealmStr> deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context)
            throws JsonParseException {
        RealmList<RealmStr> strings = new RealmList<>();
        JsonArray ja = json.getAsJsonArray();
        for (JsonElement je : ja) {
            strings.add((RealmStr) context.deserialize(je, RealmStr.class));
        }
        return strings;
    }

}
