package me.calebjones.spacelaunchnow.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.cyanea.app.CyaneaFragment;

import io.realm.Realm;

public class BaseFragment extends CyaneaFragment {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public Realm getRealm() {
        if (realm != null && !realm.isClosed()){
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    public void setScreenName(String screenName){
        this.screenName = screenName;
    }
}
