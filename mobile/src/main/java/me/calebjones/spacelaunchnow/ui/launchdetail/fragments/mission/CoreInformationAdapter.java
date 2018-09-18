package me.calebjones.spacelaunchnow.ui.launchdetail.fragments.mission;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.Stage;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class CoreInformationAdapter extends RecyclerView.Adapter<CoreInformationAdapter.ViewHolder> {
    public int position;

    private List<Stage> launcherList;

    public CoreInformationAdapter(List<Stage> launchers) {
        launcherList = launchers;
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.core_information, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Stage stage = launcherList.get(position);
        if (stage.getLauncher() != null) {
            holder.coreNumber.setText(String.format("Core #%d Information", position + 1));
            holder.serialNumberText.setText(stage.getLauncher().getSerialNumber());
            String cap = stage.getLauncher().getStatus().substring(0, 1).toUpperCase() + stage.getLauncher().getStatus().substring(1);
            holder.statusText.setText(cap);
            holder.previousText.setText("");
            if (stage.getLauncher().isFlightProven() == null) {
                holder.flightProven.setImageResource(R.drawable.ic_question_mark);
            } else if (stage.getLauncher().isFlightProven()) {
                holder.flightProven.setImageResource(R.drawable.ic_checkmark);
            } else if (!stage.getLauncher().isFlightProven()) {
                holder.flightProven.setImageResource(R.drawable.ic_failed);
            }
            holder.previousText.setText(String.format("%d", stage.getLauncher().getPreviousFlights()));

            holder.coreLaunches.setText(String.format("View %s Launches", stage.getLauncher().getSerialNumber()));

            holder.coreLaunches.setOnClickListener(v -> Timber.v("Coming soon!"));
        }
    }


    @Override
    public int getItemCount() {
        return launcherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_core_launches)
        AppCompatButton coreLaunches;
        @BindView(R.id.core_information)
        TextView coreNumber;
        @BindView(R.id.serial_number_text)
        TextView serialNumberText;
        @BindView(R.id.status_text)
        TextView statusText;
        @BindView(R.id.previous_text)
        TextView previousText;
        @BindView(R.id.flight_proven)
        ImageView flightProven;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
