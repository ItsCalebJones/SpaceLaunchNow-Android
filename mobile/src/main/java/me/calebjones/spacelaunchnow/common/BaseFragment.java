package me.calebjones.spacelaunchnow.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;

public class BaseFragment extends Fragment {
    private Realm realm;
    private String screenName = "Unknown (Name not set)";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        super.onCreateView(inflater, container, savedInstanceState);
        return null;
    }

    @Override
    public void onAttach(Context context) {
        realm = Realm.getDefaultInstance();
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        Analytics.getInstance().sendScreenView(screenName, screenName + " started.");
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.removeAllChangeListeners();
        realm.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.getInstance().sendScreenView(screenName, screenName + " resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Analytics.getInstance().notifyGoneBackground();
    }

    public Realm getRealm() {
        return realm;
    }

    public void setScreenName(String screenName){
        this.screenName = screenName;
    }
}
