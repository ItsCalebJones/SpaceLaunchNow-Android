package me.calebjones.spacelaunchnow.common.ui.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.content.LaunchStatusUtil;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class StatusPillView extends FrameLayout {

    @BindView(R2.id.status)
    TextView status;
    @BindView(R2.id.status_pill_layout)
    CardView statusPill;
    private Context context;

    public StatusPillView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StatusPillView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusPillView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.status_pill, this);
        ButterKnife.bind(this);
        this.context = context;
    }

    public void setStatus(Launch launch) {
        status.setText(LaunchStatusUtil.getLaunchStatusTitle(context, launch.getStatus().getId()));
        statusPill.setCardBackgroundColor(LaunchStatusUtil.getLaunchStatusColor(context, launch.getStatus().getId()));
    }
}
