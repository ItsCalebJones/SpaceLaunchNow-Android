package me.calebjones.spacelaunchnow.utils.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.data.LaunchStatus;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class StatusPillView extends FrameLayout {

    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.status_pill_layout)
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
        status.setText(LaunchStatus.getLaunchStatusTitle(context, launch.getStatus().getId()));
        statusPill.setCardBackgroundColor(LaunchStatus.getLaunchStatusColor(context, launch.getStatus().getId()));
    }
}
