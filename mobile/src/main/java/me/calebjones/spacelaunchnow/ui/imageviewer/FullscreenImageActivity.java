package me.calebjones.spacelaunchnow.ui.imageviewer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import android.transition.Transition;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenImageActivity extends BaseActivity {


    private String imageURL;
    private Bitmap bitmap;
    private PhotoView photoView;

    public FullscreenImageActivity() {
        super("Fullscreen Image Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_image);

        photoView = (PhotoView) findViewById(R.id.image);
        if (getIntent().getStringExtra("imageURL") != null) {
            imageURL = getIntent().getStringExtra("imageURL");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {
                    }

                    @Override
                    public void onTransitionPause(Transition transition) {
                    }

                    @Override
                    public void onTransitionResume(Transition transition) {
                    }

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onTransitionEnd(Transition transition) {
                        getWindow().getEnterTransition().removeListener(this);

                        // load the full version, crossfading from the thumbnail image
                        Glide.with(getApplicationContext())
                                .load(imageURL)
                                .into(photoView);

                    }
                });
            } else {
                // load the full version, crossfading from the thumbnail image
                GlideApp.with(getApplicationContext())
                        .load(imageURL)
                        .into(photoView);
            }
        } else if (getIntent().getStringExtra("image") != null){
            String image = getIntent().getStringExtra("image");
            if (image.contains("btc")){
                GlideApp.with(getApplicationContext())
                        .load(R.drawable.btc_wallet)
                        .into(photoView);
            } else if (image.contains("ltc")){
                GlideApp.with(getApplicationContext())
                        .load(R.drawable.ltc_wallet)
                        .into(photoView);
            } else if (image.contains("eth")){
                GlideApp.with(getApplicationContext())
                        .load(R.drawable.eth_wallet)
                        .into(photoView);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
