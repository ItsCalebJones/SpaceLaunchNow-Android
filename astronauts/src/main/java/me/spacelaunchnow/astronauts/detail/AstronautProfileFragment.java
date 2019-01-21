package me.spacelaunchnow.astronauts.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
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
        astronautInstagramButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_instagram).sizeDp(24));
        astronautWikiButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_wikipedia_w).sizeDp(24));
        astronautTwitterButton.setImageDrawable(new IconicsDrawable(context).icon(FontAwesome.Icon.faw_twitter).sizeDp(24));
        if (astronaut.getDateOfBirth() != null) {
            astronautBorn.setText(String.format("Born: %s", astronaut.getDateOfBirth()));
        }
        if (astronaut.getDateOfDeath() != null) {
            astronautBorn.setText(String.format("Born: %s", astronaut.getDateOfDeath()));
        }

        try {
            Agency agency = astronaut.getAgency();

            Timber.v("Setting up views...");
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

}
