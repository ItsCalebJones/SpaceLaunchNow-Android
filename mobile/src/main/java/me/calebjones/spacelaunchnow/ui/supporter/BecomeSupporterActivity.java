package me.calebjones.spacelaunchnow.ui.supporter;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.Utils;

public class BecomeSupporterActivity extends AppCompatActivity {

    @BindView(R.id.support_button)
    AppCompatButton supportButton;
    @BindView(R.id.patreon_button)
    AppCompatButton patreonButton;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.exit_button)
    AppCompatButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.supporter_year_overview);
        ButterKnife.bind(this);

        if (SupporterHelper.isSupporter()) {
            description.setText(getString(R.string.supporter_thank_you_2019));
        } else {
            description.setText(getString(R.string.thank_you_2019));
        }

        back.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_arrow_back)
                .color(ThemeHelper.getIconColor(this))
                .sizeDp(24));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick({R.id.support_button, R.id.patreon_button, R.id.back, R.id.exit_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.support_button:
                Intent intent = new Intent(this, SupporterActivity.class);
                startActivity(intent);
                break;
            case R.id.patreon_button:
                Utils.openCustomTab(this, getApplicationContext(),
                        "https://www.patreon.com/spacelaunchnow");
                break;
            case R.id.back:
            case R.id.exit_button:
                onBackPressed();
                break;
        }
    }
}
