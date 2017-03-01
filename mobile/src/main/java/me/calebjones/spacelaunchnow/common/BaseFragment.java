package me.calebjones.spacelaunchnow.common;

import android.support.v4.app.Fragment;

import io.realm.Realm;
import timber.log.Timber;

public class BaseFragment extends Fragment {
    private Realm realm;

    @Override
    public void onStart() {
        super.onStart();
        Timber.v("BaseFragment - onStart");
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.v("BaseFragment - onStop");
        realm.removeAllChangeListeners();
        realm.close();
    }

    public Realm getRealm() {
        return realm;
    }
}
