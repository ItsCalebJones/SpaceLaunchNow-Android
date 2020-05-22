package me.calebjones.spacelaunchnow.astronauts.detail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.launches.agency.AgencyLaunchActivity;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;
import timber.log.Timber;

public class AstronautProfileFragment extends BaseFragment {

    @BindView(R2.id.astronaut_bio_text)
    TextView astronautBio;
    @BindView(R2.id.astronaut_twitter_button)
    AppCompatImageButton astronautTwitterButton;
    @BindView(R2.id.astronaut_instagram_button)
    AppCompatImageButton astronautInstagramButton;
    @BindView(R2.id.astronaut_wiki_button)
    AppCompatImageButton astronautWikiButton;
    @BindView(R2.id.astronaut_status)
    TextView astronautStatus;
    @BindView(R2.id.lsp_logo)
    ImageView lspLogo;
    @BindView(R2.id.lsp_name)
    TextView lspName;
    @BindView(R2.id.lsp_type)
    TextView lspType;
    @BindView(R2.id.lsp_summary)
    TextView lspSummary;
    @BindView(R2.id.lsp_infoButton_one)
    AppCompatButton lspInfoButtonOne;
    @BindView(R2.id.lsp_wikiButton_one)
    AppCompatButton lspWikiButtonOne;
    @BindView(R2.id.lsp_card)
    CardView lspCard;
    @BindView(R2.id.lsp_administrator)
    TextView lspAdministrator;
    @BindView(R2.id.lsp_founded_year)
    TextView lspFoundedYear;
    @BindView(R2.id.lsp_agency)
    AppCompatButton lspAgency;
    @BindView(R2.id.astronaut_born)
    TextView astronautBorn;
    @BindView(R2.id.astronaut_died)
    TextView astronautDied;
    private AstronautDetailViewModel mViewModel;
    private Unbinder unbinder;
    private Context context;

    public static AstronautProfileFragment newInstance() {
        return new AstronautProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.astronaut_profile_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(AstronautDetailViewModel.class);
        // update UI
        mViewModel.getAstronaut().observe(this, this::setAstronaut);
    }

    private void setAstronaut(Astronaut astronaut) {
        astronautBio.setText(astronaut.getBio());
        astronautStatus.setText(astronaut.getStatus().getName());
        int color = ThemeHelper.getIconColor(getActivity());
        astronautInstagramButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_instagram).sizeDp(24).color(color));
        astronautWikiButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_wikipedia_w).sizeDp(24).color(color));
        astronautTwitterButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_twitter).sizeDp(24).color(color));

        if (astronaut.getWiki() != null){
            astronautWikiButton.setVisibility(View.VISIBLE);
            astronautWikiButton.setOnClickListener(v -> {
                try {
                    Uri webpage = Uri.parse(astronaut.getWiki());
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else {
            astronautWikiButton.setVisibility(View.GONE);
        }

        if (astronaut.getInstagram() != null){
            astronautInstagramButton.setVisibility(View.VISIBLE);
            astronautInstagramButton.setOnClickListener(v -> {
                try {
                    Uri webpage = Uri.parse(astronaut.getInstagram());
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else {
            astronautInstagramButton.setVisibility(View.GONE);
        }

        if (astronaut.getTwitter() != null){
            astronautTwitterButton.setVisibility(View.VISIBLE);
            astronautTwitterButton.setOnClickListener(v -> {
                try {
                    Uri webpage = Uri.parse(astronaut.getTwitter());
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else {
            astronautTwitterButton.setVisibility(View.GONE);
        }

        String bornDate = null;
        String deathDate = null;

        if (astronaut.getDateOfBirth() != null) {
            bornDate = DateFormat.getDateInstance(DateFormat.LONG).format(astronaut.getDateOfBirth());
        }
        if (astronaut.getDateOfDeath() != null) {
            deathDate = DateFormat.getDateInstance(DateFormat.LONG).format(astronaut.getDateOfDeath());
        }




        if (bornDate != null && deathDate == null) {
            int bornYear = astronaut.getDateOfBirth().getYear();
            int currentYear = Calendar.getInstance().getTime().getYear();
            astronautBorn.setText(getString(R.string.born, bornDate, currentYear - bornYear));
            astronautDied.setVisibility(View.GONE);
        }
        if (deathDate != null && bornDate != null) {
            int bornYear = astronaut.getDateOfBirth().getYear();
            int diedYear =  astronaut.getDateOfDeath().getYear();
            astronautBorn.setText(getString(R.string.born_one_argument, bornDate));
            astronautDied.setText(getString(R.string.died_two_arguments, deathDate, diedYear - bornYear));
            astronautDied.setVisibility(View.VISIBLE);
        }

        try {
            Agency agency = astronaut.getAgency();

            Timber.v("Setting up views...");
            if (agency != null) {
                lspCard.setVisibility(View.VISIBLE);

                lspAgency.setText(String.format(this.getString(me.calebjones.spacelaunchnow.common.R.string.view_rocket_launches), agency.getName()));
                if (agency.getLogoUrl() != null) {
                    lspLogo.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(agency.getLogoUrl())
                            .centerInside()
                            .into(lspLogo);
                } else {
                    lspLogo.setVisibility(View.GONE);
                }
                lspName.setText(agency.getName());
                lspType.setText(agency.getType());
                if (agency.getAdministrator() != null) {
                    lspAdministrator.setText(String.format("%s", agency.getAdministrator()));
                } else {
                    lspAdministrator.setText(me.calebjones.spacelaunchnow.common.R.string.unknown_administrator);
                }
                if (agency.getFoundingYear() != null) {
                    lspFoundedYear.setText(String.format(getString(me.calebjones.spacelaunchnow.common.R.string.founded_in), agency.getFoundingYear()));
                } else {
                    lspFoundedYear.setText(me.calebjones.spacelaunchnow.common.R.string.unknown_year);
                }
                lspSummary.setText(agency.getDescription());
                if (agency.getInfoUrl() == null) {
                    lspInfoButtonOne.setVisibility(View.GONE);
                }

                if (agency.getWikiUrl() == null) {
                    lspWikiButtonOne.setVisibility(View.GONE);
                }
                lspAgency.setVisibility(View.VISIBLE);
            } else {
                lspCard.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            Timber.e(e);
        }
        astronaut.getAgency();
        astronaut.getBio();
        astronaut.getDateOfBirth();
        astronaut.getDateOfDeath();
        astronaut.getInstagram();
        astronaut.getTwitter();
        astronaut.getWiki();
        astronaut.getStatus();
    }

    @OnClick(R2.id.lsp_agency)
    void launchesClicked(){
        try {
            Intent intent = new Intent(context, AgencyLaunchActivity.class);
            intent.putExtra("lspName", mViewModel.getAstronaut().getValue().getAgency().getName());
            startActivity(intent);
        } catch (NullPointerException e){
            Timber.e(e);
        }
    }
}
