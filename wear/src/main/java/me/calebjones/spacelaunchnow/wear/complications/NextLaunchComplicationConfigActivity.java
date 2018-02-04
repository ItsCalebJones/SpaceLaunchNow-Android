package me.calebjones.spacelaunchnow.wear.complications;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ProviderUpdateRequester;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.pixplicity.easyprefs.library.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.content.SwitchPreference;


public class NextLaunchComplicationConfigActivity extends WearableActivity {

    @BindView(R.id.switch_all)
    Switch switchAll;
    @BindView(R.id.switch_spacex)
    Switch switchSpacex;
    @BindView(R.id.switch_nasa)
    Switch switchNasa;
    @BindView(R.id.switch_ula)
    Switch switchUla;
    @BindView(R.id.switch_roscosmos)
    Switch switchRoscosmos;
    @BindView(R.id.switch_cnsa)
    Switch switchCnsa;
    @BindView(R.id.button_ok)
    Button buttonOk;

    private SwitchPreference switchPreference;
    private int complicationId;
    private ComponentName provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_launch_complication_configuration);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            complicationId = extras.getInt("android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID");
            provider = extras.getParcelable(ComplicationProviderService.EXTRA_CONFIG_PROVIDER_COMPONENT);
            switchPreference = SwitchPreference.getInstance(this, complicationId);

            if (switchPreference.getAllSwitch()) {
                switchAll.setChecked(true);
                switchSpacex.setChecked(true);
                switchUla.setChecked(true);
                switchNasa.setChecked(true);
                switchRoscosmos.setChecked(true);
                switchCnsa.setChecked(true);
            } else {
                switchNasa.setChecked(switchPreference.getSwitchNasa());
                switchUla.setChecked(switchPreference.getSwitchULA());
                switchRoscosmos.setChecked(switchPreference.getSwitchRoscosmos());
                switchSpacex.setChecked(switchPreference.getSwitchSpaceX());
                switchCnsa.setChecked(switchPreference.getSwitchCNSA());
            }
        } else {
            Intent cancelIntent = new Intent();
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
        }
    }

    @OnClick({R.id.switch_all, R.id.switch_spacex, R.id.switch_nasa, R.id.switch_ula,
            R.id.switch_roscosmos, R.id.switch_cnsa, R.id.button_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_all:
                switchPreference.setAllSwitch(switchAll.isChecked());
                if (switchAll.isChecked()){
                    switchSpacex.setChecked(true);
                    switchUla.setChecked(true);
                    switchNasa.setChecked(true);
                    switchRoscosmos.setChecked(true);
                    switchCnsa.setChecked(true);
                }
                break;
            case R.id.switch_spacex:
                if (switchAll.isChecked() && !switchSpacex.isChecked()){
                    switchAll.setChecked(false);
                    switchPreference.setAllSwitch(switchAll.isChecked());
                }
                switchPreference.setSwitchSpaceX(switchSpacex.isChecked());
                break;
            case R.id.switch_nasa:
                if (switchAll.isChecked() && !switchNasa.isChecked()){
                    switchAll.setChecked(false);
                    switchPreference.setAllSwitch(switchAll.isChecked());
                }
                switchPreference.setSwitchNasa(switchNasa.isChecked());
                break;
            case R.id.switch_ula:
                if (switchAll.isChecked() && !switchUla.isChecked()){
                    switchAll.setChecked(false);
                    switchPreference.setAllSwitch(switchAll.isChecked());
                }
                switchPreference.setSwitchULA(switchUla.isChecked());
                break;
            case R.id.switch_roscosmos:
                if (switchAll.isChecked() && !switchRoscosmos.isChecked()){
                    switchAll.setChecked(false);
                    switchPreference.setAllSwitch(switchAll.isChecked());
                }
                switchPreference.setSwitchRoscosmos(switchRoscosmos.isChecked());
                break;
            case R.id.switch_cnsa:
                if (switchAll.isChecked() && !switchCnsa.isChecked()){
                    switchAll.setChecked(false);
                    switchPreference.setAllSwitch(switchAll.isChecked());
                }
                switchPreference.setSwitchCNSA(switchCnsa.isChecked());
                break;
            case R.id.button_ok:
                switchPreference.setConfigured(true);
                new ProviderUpdateRequester(this, new ComponentName(this, NextLaunchComplicationProvider.class)).requestUpdate(complicationId);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }


}
