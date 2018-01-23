package me.calebjones.spacelaunchnow.wear.complications;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.wear.content.ComplicationContentManager;
import me.calebjones.spacelaunchnow.wear.content.SwitchPreference;
import me.calebjones.spacelaunchnow.wear.ui.launchdetail.LaunchDetail;
import me.calebjones.spacelaunchnow.wear.utils.SupporterHelper;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.wear.model.Constants.*;

public class NextLaunchComplicationProvider extends ComplicationProviderService implements ComplicationContentManager.ContentCallback {

    private SwitchPreference switchPreference;
    private Realm realm;
    private int dataType;
    private ComplicationData complicationData;
    private ComplicationManager complicationManager;
    private PendingIntent configurationPendingIntent;
    private PendingIntent supporterPendingIntent;
    private PendingIntent openLaunchDetail;
    private ComplicationContentManager contentManager;
    private int complicationId;

    static PendingIntent getConfigurationIntent(Context context, ComponentName provider, int complicationId) {
        Intent intent = new Intent(context, NextLaunchComplicationConfigActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_CONFIG_PROVIDER_COMPONENT, provider);
        intent.putExtra("android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID", complicationId);

        return PendingIntent.getActivity(context, complicationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    static PendingIntent getOpenLaunchDetail(Context context, int launchId, int complicationId) {
        Intent intent = new Intent(context, LaunchDetail.class);
        intent.putExtra("launchId", launchId);

        return PendingIntent.getActivity(context, complicationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    @Override
    public void onComplicationActivated(
            int complicationId, int dataType, ComplicationManager complicationManager) {
        Timber.v("onComplicationActivated(): %s", complicationId);
    }

    @Override
    public void onComplicationDeactivated (int complicationId){
        Timber.v("onComplicationActivated(): %s", complicationId);
    }


    @Override
    public void onComplicationUpdate(int complicationId, int dataType, ComplicationManager complicationManager) {
        this.dataType = dataType;
        this.complicationId = complicationId;
        this.complicationManager = complicationManager;

        contentManager = new ComplicationContentManager(getApplicationContext(), this);

        // Create Tap Action so that the user can trigger an update by tapping the complication.
        ComponentName thisProvider = new ComponentName(this, getClass());
        // We pass the complication id, so we can only update the specific complication tapped.
        configurationPendingIntent = getConfigurationIntent(this, thisProvider, complicationId);

        supporterPendingIntent = ComplicationTapBroadcastReceiver.getToggleIntent(this, thisProvider, complicationId);


        complicationData = null;

        // Retrieves your data, in this case, we grab an incrementing number from SharedPrefs.
        realm = Realm.getDefaultInstance();
        switchPreference = SwitchPreference.getInstance(this, complicationId);

        if(!SupporterHelper.isSupporter()){
            switch (dataType) {
                case ComplicationData.TYPE_SHORT_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                            .setShortTitle(ComplicationText.plainText("Become"))
                            .setShortText(ComplicationText.plainText("Supporter"))
                            .setTapAction(supporterPendingIntent)
                            .build();
                    break;
                case ComplicationData.TYPE_LONG_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                            .setLongText(ComplicationText.plainText("Become a Supporter"))
                            .setTapAction(supporterPendingIntent)
                            .build();
                    break;
                default:
                    Timber.d("Unexpected complication type %s", dataType);
            }
            if (complicationData != null) {
                complicationManager.updateComplicationData(complicationId, complicationData);

            } else {
                // If no data is sent, we still need to inform the ComplicationManager, so
                // the update job can finish and the wake lock isn't held any longer.
                complicationManager.noUpdateRequired(complicationId);
            }
            realm.close();
            return;
        }

        if (!switchPreference.isConfigured()) {
            switch (dataType) {
                case ComplicationData.TYPE_SHORT_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                            .setShortTitle(ComplicationText.plainText("Setup"))
                            .setShortText(ComplicationText.plainText("Tap to"))
                            .setTapAction(configurationPendingIntent)
                            .build();
                    break;
                case ComplicationData.TYPE_LONG_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                            .setLongText(ComplicationText.plainText("Tap to Setup"))
                            .setTapAction(configurationPendingIntent)
                            .build();
                    break;
                default:
                    Timber.d("Unexpected complication type %s", dataType);
            }

            if (complicationData != null) {
                complicationManager.updateComplicationData(complicationId, complicationData);

            } else {
                // If no data is sent, we still need to inform the ComplicationManager, so
                // the update job can finish and the wake lock isn't held any longer.
                complicationManager.noUpdateRequired(complicationId);
            }
            realm.close();
            return;
        }

        //TODO network call here get next 10?
        if (contentManager.isNetworkAvailable() && contentManager.shouldGetFresh(0)) {
            contentManager.getFreshData();
        } else {
            dataLoaded();
        }
    }

    @Override
    public void dataLoaded() {
        RealmResults<Launch> launches;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        Date date = calendar.getTime();

        if (switchPreference.getAllSwitch()) {
            launches = realm.where(Launch.class).greaterThanOrEqualTo("net", date).sort("net").findAll();
        } else {
            boolean firstGroup = true;
            RealmQuery<Launch> query = realm.where(Launch.class).greaterThanOrEqualTo("net", date);
            if (switchPreference.getSwitchCNSA()) {
                if (!firstGroup) {
                    query.or();
                } else {
                    query.equalTo("lsp.id", AGENCY_CNSA);
                    firstGroup = false;
                }
            }
            if (switchPreference.getSwitchNasa()) {
                if (!firstGroup) {
                    query.or();
                } else {
                    query.equalTo("lsp.id", AGENCY_NASA);
                    firstGroup = false;
                }
            }
            if (switchPreference.getSwitchRoscosmos()) {
                if (!firstGroup) {
                    query.or();
                } else {
                    query.equalTo("lsp.id", AGENCY_ROSCOSMOS);
                    firstGroup = false;
                }
            }
            if (switchPreference.getSwitchSpaceX()) {
                if (!firstGroup) {
                    query.or();
                } else {
                    query.equalTo("lsp.id", AGENCY_SPACEX);
                    firstGroup = false;
                }
            }
            if (switchPreference.getSwitchULA()) {
                if (!firstGroup) {
                    query.or();
                }
                query.equalTo("lsp.id", AGENCY_ULA);
            }
            launches = query.findAll();
        }
        Launch launch = null;
        if (launches != null && launches.size() > 0) {
            launch = launches.first();
        } else if (launches != null && launches.size() == 0){
            if (switchPreference.getSwitchULA() && contentManager.shouldGetFresh(AGENCY_ULA)){
                contentManager.getFreshData(AGENCY_ULA);
                return;
            }
            if (switchPreference.getSwitchSpaceX()  && contentManager.shouldGetFresh(AGENCY_SPACEX)){
                contentManager.getFreshData(AGENCY_SPACEX);
                return;
            }
            if (switchPreference.getSwitchNasa()  && contentManager.shouldGetFresh(AGENCY_NASA)){
                contentManager.getFreshData(AGENCY_NASA);
                return;
            }
            if (switchPreference.getSwitchRoscosmos() && contentManager.shouldGetFresh(AGENCY_ROSCOSMOS)){
                contentManager.getFreshData(AGENCY_ROSCOSMOS);
                return;
            }
            if (switchPreference.getSwitchCNSA()  && contentManager.shouldGetFresh(AGENCY_CNSA)){
                contentManager.getFreshData(AGENCY_CNSA);
                return;
            }
        }


        if (launch != null) {
            openLaunchDetail = getOpenLaunchDetail(this, launch.getId(), complicationId);

            Date windowClosed = launch.getWindowend();
            Date windowOpen = launch.getWindowstart();

            ComplicationText shortCountdownText;
            ComplicationText longCountdownText;
            ComplicationText.TimeDifferenceBuilder countdown = new ComplicationText.TimeDifferenceBuilder();
            countdown.setReferencePeriodEnd(windowClosed.getTime()).setReferencePeriodStart(windowOpen.getTime());
            shortCountdownText = countdown.setStyle(ComplicationText.DIFFERENCE_STYLE_SHORT_WORDS_SINGLE_UNIT).build();
            longCountdownText = countdown.setStyle(ComplicationText.DIFFERENCE_STYLE_WORDS_SINGLE_UNIT).build();

            switch (dataType) {
                case ComplicationData.TYPE_SHORT_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                            .setShortTitle(ComplicationText.plainText(launch.getLsp().getAbbrev()))
                            .setShortText(shortCountdownText)
                            .setTapAction(openLaunchDetail)
                            .build();
                    break;
                case ComplicationData.TYPE_LONG_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                            .setLongText(longCountdownText)
                            .setLongTitle(ComplicationText.plainText(launch.getName()))
                            .setTapAction(openLaunchDetail)
                            .build();
                    break;
                default:
                    Timber.d("Unexpected complication type %s", dataType);
            }

            if (complicationData != null) {
                complicationManager.updateComplicationData(complicationId, complicationData);

            } else {
                // If no data is sent, we still need to inform the ComplicationManager, so
                // the update job can finish and the wake lock isn't held any longer.
                complicationManager.noUpdateRequired(complicationId);
            }

        } else {
            switch (dataType) {
                case ComplicationData.TYPE_SHORT_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                            .setShortTitle(ComplicationText.plainText("Launches"))
                            .setShortText(ComplicationText.plainText("No"))
                            .setTapAction(openLaunchDetail)
                            .build();
                    break;
                case ComplicationData.TYPE_LONG_TEXT:
                    complicationData = new ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                            .setLongText(ComplicationText.plainText("No Launches Available"))
                            .setTapAction(openLaunchDetail)
                            .build();
                    break;
                default:
                    Timber.d("Unexpected complication type %s", dataType);
            }

            if (complicationData != null) {
                complicationManager.updateComplicationData(complicationId, complicationData);

            } else {
                // If no data is sent, we still need to inform the ComplicationManager, so
                // the update job can finish and the wake lock isn't held any longer.
                complicationManager.noUpdateRequired(complicationId);
            }
        }
        realm.close();

    }

    @Override
    public void errorLoading(String error) {
        dataLoaded();
    }
}
