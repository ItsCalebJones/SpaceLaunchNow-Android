package me.calebjones.spacelaunchnow.content.loader;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.legacy.Launch;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;

public class PreviousLoader extends AsyncTaskLoader<List<LaunchRealm>> {
    private Realm realm;

    public PreviousLoader(Context context) {
        super(context);
    }

    @Override
    public List<LaunchRealm> loadInBackground() {
        Date date = new Date();
        realm = Realm.getDefaultInstance();
        return realm.where(LaunchRealm.class)
                .lessThan("net", date)
                .findAllSorted("net", Sort.DESCENDING);
    }
}