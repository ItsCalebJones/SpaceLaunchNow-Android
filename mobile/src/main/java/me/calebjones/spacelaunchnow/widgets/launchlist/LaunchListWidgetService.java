package me.calebjones.spacelaunchnow.widgets.launchlist;

import android.content.Intent;
import android.widget.RemoteViewsService;

import timber.log.Timber;

public class LaunchListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.v("onGetViewFactory - Intent received.");
        return(new LaunchListFactory(this.getApplicationContext(), intent));
    }
}