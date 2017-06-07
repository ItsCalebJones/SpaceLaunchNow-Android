package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.calebjones.spacelaunchnow.content.DataRepositoryManager;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        DataRepositoryManager dataRepositoryManager = new DataRepositoryManager(context);
        dataRepositoryManager.syncBackground();
    }
}
