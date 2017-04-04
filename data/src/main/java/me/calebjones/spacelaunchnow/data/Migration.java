package me.calebjones.spacelaunchnow.data;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import me.calebjones.spacelaunchnow.data.models.Constants;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.d("Migration", "migrate: From " + oldVersion + " to " + newVersion);

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        if (oldVersion <= Constants.DB_SCHEMA_VERSION_OLD) {
            // Change type from String to int
            schema.get("Launch").addField("locationid", int.class);
            oldVersion++;
        }
    }
}
