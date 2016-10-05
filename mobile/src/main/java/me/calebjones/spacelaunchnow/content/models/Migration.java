package me.calebjones.spacelaunchnow.content.models;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import timber.log.Timber;

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        Timber.v("Starting migration update from %s to %s", oldVersion, newVersion);

        if (oldVersion == 0){
            Timber.v("Running migration from Schema version 0 to 1.");
            schema.get("LaunchRealm")
                    .renameField("favorite", "syncCalendar")
                    .addField("eventID", Integer.class);
            oldVersion++;
        }

        if (oldVersion == 1){
            Timber.v("Running migration from Schema version 1 to 2.");
            schema.get("LaunchRealm")
                    .addField("userToggledCalendar", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("userToggledCalendar", false);
                        }
                    });
            oldVersion++;
        }

        if (oldVersion == 2){
            Timber.v("Running migration from Schema version 2 to 3.");
            schema.get("LaunchRealm")
                    .addField("notifiable", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("notifiable", false);
                        }
                    })
                    .addField("userToggledNotifiable", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("userToggledNotifiable", false);
                        }
                    });
            oldVersion++;
        }

        if (oldVersion == 3){
            Timber.v("Running migration from Schema version 3 to 4.");
            // Create a new class
            RealmObjectSchema petSchema = schema.create("LaunchNotification")
                    .addField("id", Integer.class, FieldAttribute.PRIMARY_KEY)
                    .addField("isNotifiedDay", boolean.class)
                    .addField("isNotifiedHour", boolean.class)
                    .addField("isNotifiedTenMinute", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("isNotifiedDay", false);
                        }
                    })
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("isNotifiedHour", false);
                        }
                    })
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("isNotifiedTenMinute", false);
                        }
                    });
            oldVersion++;
        }

        if (oldVersion == 4){
            Timber.v("Running migration from Schema version 4 to 5.");
            schema.get("LaunchRealm")
                    .addField("eventID", Integer.class);
            oldVersion++;
        }

        if (oldVersion == 5){
            Timber.v("Running migration from Schema version 5 to 6.");
            schema.get("CalendarItem")
                    .removeField("name")
                    .removeField("location")
                    .removeField("description")
                    .removeField("start")
                    .removeField("end")
                    .removeField("id")
                    .renameField("launchID", "id")
                    .addField("accountName", String.class);
            oldVersion++;
        }

        if (oldVersion == 6){
            Timber.v("Running migration from Schema version 6 to 7.");
            schema.get("LaunchRealm")
                    .addField("eventID", Integer.class);
            oldVersion++;
        }

        if (oldVersion == 7){
            Timber.v("Running migration from Schema version 7 to 8.");
            schema.get("CalendarItem")
            .transform(new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                    obj.deleteFromRealm();
                }
            });
            oldVersion++;
        }

        if (oldVersion == 8){
            Timber.v("Running migration from Schema version 8 to 9.");
            schema.get("CalendarItem")
                    .setRequired("id", true);
            oldVersion++;
        }

        Timber.v("Finished running migrations.");
    }
}
