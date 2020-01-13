package me.calebjones.spacelaunchnow.ui.changelog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mukesh.MarkdownView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.utils.Utils;

public class ChangelogActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.markdown_view)
    MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.whats_new) + " v" + Utils.getVersionName(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        markdownView.loadMarkdownFromAssets("CHANGELOG.md");
        markdownView.setOpenUrlInBrowser(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
