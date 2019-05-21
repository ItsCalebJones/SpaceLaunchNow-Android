package me.calebjones.spacelaunchnow.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.realm.Realm;

public class BaseFragment extends Fragment {
    private Realm realm;
    private String screenName = "Unknown (Name not set)";
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
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
        if (getActivity() != null) {
            firebaseAnalytics.setCurrentScreen(getActivity(), screenName, null);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public Realm getRealm() {
        if (realm != null && !realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
