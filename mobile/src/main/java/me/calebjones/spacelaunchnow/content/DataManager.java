package me.calebjones.spacelaunchnow.content;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.services.NextLaunchTracker;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchNotification;
import me.calebjones.spacelaunchnow.data.networking.LibraryClient;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.utils.Analytics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 *  This class is responsible for async loading of data via the LibraryClient and saving it to Realm for future use.
 */

public class DataManager {

    private Context context;
    public DataRepositoryManager dataRepositoryManager;
    public boolean isRunning = false;

    public DataManager(Context context) {
        this.context = context;
        this.dataRepositoryManager = new DataRepositoryManager(context, this);
    }

    public DataRepositoryManager getDataRepositoryManager(){
        return dataRepositoryManager;
    }

    public void getLaunchesByDate(final String startDate, final String endDate) {
        isRunning = true;
        LibraryClient.getInstance().getLaunchesByDate(startDate, endDate, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isRunning = false;
                        ListPreferences.getInstance(context).isFresh(true);
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_PREV_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);

                        Analytics.from(context).sendNetworkEvent(
                                Constants.ACTION_SUCCESS_PREV_LAUNCHES,
                                call.request().url().toString(),
                                true
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;

                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("error", t.getLocalizedMessage());
                broadcastIntent.setAction(Constants.ACTION_FAILURE_PREV_LAUNCHES);
                context.sendBroadcast(broadcastIntent);

                Analytics.from(context).sendNetworkEvent(
                        Constants.ACTION_FAILURE_PREV_LAUNCHES,
                        call.request().url().toString(),
                        false,
                        t.getLocalizedMessage()
                );
            }
        });
    }

    public void getLaunchesByDate(final String startDate, final String endDate, final int offset) {
        ListPreferences.getInstance(context).isFresh(true);
        isRunning = true;
        LibraryClient.getInstance().getLaunchesByDate(startDate, endDate, offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount() + offset;
                    Timber.i("getLaunchesByDate - Successful - Total: %s Offset: %s Count: %s",total, offset, count);
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isRunning = false;
                        ListPreferences.getInstance(context).isFresh(true);
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_PREV_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);

                        Analytics.from(context).sendNetworkEvent(
                                Constants.ACTION_SUCCESS_PREV_LAUNCHES,
                                call.request().url().toString(),
                                true
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;

                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("error", t.getLocalizedMessage());
                broadcastIntent.setAction(Constants.ACTION_FAILURE_PREV_LAUNCHES);
                context.sendBroadcast(broadcastIntent);

                Analytics.from(context).sendNetworkEvent(
                        Constants.ACTION_FAILURE_PREV_LAUNCHES,
                        call.request().url().toString(),
                        false,
                        t.getLocalizedMessage()
                );
            }
        });
    }

    public void getUpcomingLaunches() {
        isRunning = true;
        LibraryClient.getInstance().getUpcomingLaunches(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    if (count < total) {
                        getUpcomingLaunches(count);
                    } else {
                        isRunning = false;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_UP_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);

                        Analytics.from(context).sendNetworkEvent(Constants.ACTION_SUCCESS_UP_LAUNCHES, call.request().url().toString(), true);

                        context.startService(new Intent(context, NextLaunchTracker.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                //TODO handle errors
                Timber.e("Error: %s", t.getLocalizedMessage());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_FAILURE_UP_LAUNCHES);
                context.sendBroadcast(broadcastIntent);
                isRunning = false;

                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    public void getUpcomingLaunches(int offset) {
        isRunning = true;
        LibraryClient.getInstance().getUpcomingLaunches(offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    if (count < total) {
                        getUpcomingLaunches(count);
                    } else {
                        isRunning = false;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_UP_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);

                        context.startService(new Intent(context, NextLaunchTracker.class));

                        Analytics.from(context).sendNetworkEvent(Constants.ACTION_UPDATE_NEXT_LAUNCH, call.request().url().toString(), true);
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                //TODO handle errors
                Timber.e("Error: %s", t.getLocalizedMessage());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_FAILURE_UP_LAUNCHES);
                context.sendBroadcast(broadcastIntent);
                isRunning = false;

                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    public void getUpcomingLaunchesAll() {
        isRunning = true;

        LibraryClient.getInstance().getUpcomingLaunchesAll(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isRunning = false;

                        Analytics.from(context)
                                .sendNetworkEvent(
                                        Constants.ACTION_SUCCESS_UP_LAUNCHES,
                                        call.request().url().toString(),
                                        true
                                );

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_UP_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);

                        context.startService(new Intent(context, NextLaunchTracker.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;

                Analytics.from(context)
                        .sendNetworkEvent(
                                Constants.ACTION_FAILURE_UP_LAUNCHES,
                                call.request().url().toString(),
                                false,
                                t.getLocalizedMessage()
                        );

                context.startService(new Intent(context, NextLaunchTracker.class));

            }
        });
    }

    public void getUpcomingLaunchesAll(final int offset) {
        isRunning = true;

        LibraryClient.getInstance().getUpcomingLaunchesAll(offset, new Callback<LaunchResponse>() {

            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {
                if (launchResponse.isSuccessful()) {
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), false);
                    int total = launchResponse.body().getTotal();
                    int count = launchResponse.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isRunning = false;

                        Analytics.from(context)
                                .sendNetworkEvent(
                                        Constants.ACTION_SUCCESS_UP_LAUNCHES,
                                        call.request().url().toString(),
                                        true
                                );

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Constants.ACTION_SUCCESS_UP_LAUNCHES);
                        context.sendBroadcast(broadcastIntent);
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;

                Analytics.from(context)
                        .sendNetworkEvent(
                                Constants.ACTION_FAILURE_UP_LAUNCHES,
                                call.request().url().toString(),
                                false,
                                t.getLocalizedMessage()
                        );

                context.startService(new Intent(context, NextLaunchTracker.class));

            }
        });
    }

    public void getNextLaunches() {
        isRunning = true;

        LibraryClient.getInstance().getNextLaunches(new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> launchResponse) {

                if (launchResponse.isSuccessful()) {
                    saveLaunchesToRealm(launchResponse.body().getLaunches(), true);
                    Analytics.from(context)
                            .sendNetworkEvent(
                                    Constants.ACTION_SUCCESS_UP_LAUNCHES,
                                    call.request().url().toString(),
                                    true
                            );

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Constants.ACTION_SUCCESS_UP_LAUNCHES);
                    context.sendBroadcast(broadcastIntent);

                    context.startService(new Intent(context, NextLaunchTracker.class));
                } else {
                    //TODO Handle this case
                    isRunning = false;
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;

                Analytics.from(context)
                        .sendNetworkEvent(
                                Constants.ACTION_FAILURE_UP_LAUNCHES,
                                call.request().url().toString(),
                                false,
                                t.getLocalizedMessage()
                        );
                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    public void getLaunchById(int id) {
        isRunning = true;

        LibraryClient.getInstance().getLaunchById(id, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    saveLaunchesToRealm(response.body().getLaunches(), false);

                    Analytics.from(context)
                            .sendNetworkEvent(
                                    Constants.ACTION_SUCCESS_UP_LAUNCHES + "_BY_ID",
                                    call.request().url().toString(),
                                    true
                            );

                } else {
                    //TODO Handle error.
                    isRunning = false;
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isRunning = false;
                Analytics.from(context)
                        .sendNetworkEvent(
                                Constants.ACTION_FAILURE_UP_LAUNCHES + "_BY_ID",
                                call.request().url().toString(),
                                false,
                                t.getLocalizedMessage()
                        );
            }
        });
    }

    private void saveLaunchesToRealm(Launch[] launches, boolean mini) {
        isRunning = true;
        Realm mRealm = Realm.getDefaultInstance();

        for (Launch item : launches) {
            mRealm.beginTransaction();
            Launch previous = mRealm.where(Launch.class)
                    .equalTo("id", item.getId())
                    .findFirst();
            if (previous != null) {
                if ((!previous.getNet().equals(item.getNet()) || (previous.getStatus().intValue() != item.getStatus().intValue()))) {
                    Timber.v("%s status has changed.", item.getName());
                    LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                    if (notification != null) {
                        notification.resetNotifiers();
                        mRealm.copyToRealmOrUpdate(notification);
                    }
                }
                item.setEventID(previous.getEventID());
                item.setSyncCalendar(previous.syncCalendar());
                item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
            }
            if (item.getLocation() !=null) {
                item.getLocation().setPrimaryID();
            }
            Timber.v("Saving item: %s", item.getName());
            mRealm.copyToRealmOrUpdate(item);
            mRealm.commitTransaction();

            if (mini) {
                getLaunchById(item.getId());
            }
        }
        syncNotifiers();
        isRunning = false;
    }

    public  void syncNotifiers() {
        isRunning = true;
        RealmResults<Launch> launchRealms;
        Date date = new Date();

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Realm mRealm = Realm.getDefaultInstance();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
        }

        for (final Launch launchRealm : launchRealms) {
            if (!launchRealm.isUserToggledNotifiable() && !launchRealm.isNotifiable()) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchRealm.setNotifiable(true);
                    }
                });
            }
        }
        mRealm.close();
        isRunning = false;
    }
}
