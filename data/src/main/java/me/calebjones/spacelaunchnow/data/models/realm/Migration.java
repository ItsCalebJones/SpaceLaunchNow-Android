package me.calebjones.spacelaunchnow.data.models.realm;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Timber.d("Migrate - From %s to %s", oldVersion, newVersion);

        // Access the Realm schema in order to create, modify or delete classes and their fields.
        RealmSchema schema = realm.getSchema();

        Timber.i("Current Schema - Version %s", oldVersion);
        for(RealmObjectSchema objectSchema : schema.getAll()){
            Timber.d("Name: %s Fields: %s", objectSchema.getClassName(), objectSchema.getFieldNames());
        }
        /*
         Migration for Version 1.6.0
         Releases prior to 1.6.0 do not need to be migrated.
        */
        if (oldVersion <= Constants.DB_SCHEMA_VERSION_1_5_5) {
            // Change type from String to int
                schema.get("Launch")
                        .addField("locationid", Integer.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                if (obj.getObject("location") != null) {
                                    obj.set("locationid", obj.getObject("location").get("id"));
                                    Timber.v("Adding Locationg ID: %s for %s",
                                             obj.getObject("location").get("id"),
                                             obj.getObject("location").get("name"));
                                } else {
                                    obj.set("locationid", 0);
                                    Timber.v("Unable to find location id, setting to default 0");
                                }
                            }
                        });
            oldVersion++;
        }

        Timber.i("Final Schema - Version %s", newVersion);
        for (RealmObjectSchema objectSchema : schema.getAll()) {
            Timber.d("Name: %s Fields: %s", objectSchema.getClassName(), objectSchema.getFieldNames());

        }
    }
}
