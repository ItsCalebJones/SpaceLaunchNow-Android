package me.calebjones.spacelaunchnow.ui.fragment;

import android.support.v4.app.Fragment;

import io.realm.Realm;

public class BaseFragment extends Fragment {
    private Realm realm;

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    public Realm getRealm() {
        return realm;
    }
}
