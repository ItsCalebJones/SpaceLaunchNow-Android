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
                    .renameField("favorite", "syncCalendar");

            schema.get("LaunchRealm")
                    .addField("userToggledCalendar", boolean.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            obj.setBoolean("userToggledCalendar", false);
                        }
                    });

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

            RealmObjectSchema notificationSchema = schema.create("LaunchNotification")
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

        Timber.v("Finished running migrations.");
    }
}
