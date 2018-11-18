package me.calebjones.spacelaunchnow.content.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.utils.youtube.YouTubeAPIHelper;
import me.calebjones.spacelaunchnow.utils.youtube.models.Video;
import me.calebjones.spacelaunchnow.utils.youtube.models.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.SimpleListVH> implements MDAdapter {

    public interface Callback {
        void onListItemSelected(int index, MaterialSimpleListItem item, boolean longClick);
    }

    private MaterialDialog dialog;
    private List<MaterialSimpleListItem> mItems;
    private Callback mCallback;
    private Context context;

    public DialogAdapter(Context context, Callback callback) {
        this.context = context;
        mItems = new ArrayList<>(4);
        mCallback = callback;
    }

    public void add(MaterialSimpleListItem item) {
        try {
            String url = item.getContent().toString();
            URI uri = new URI(url);
            mItems.add(item);
            if (uri.getHost().contains("youtube")) {
                item = new MaterialSimpleListItem
                        .Builder(context)
                        .content("YouTube")
                        .build();
                getFullName((mItems.size() - 1), url);
            } else {
                String name = uri.getHost();
                item = new MaterialSimpleListItem
                        .Builder(context)
                        .content(name)
                        .build();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            mItems.add(item);
        }
        notifyItemInserted(mItems.size() - 1);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public MaterialSimpleListItem getItem(int index) {
        return mItems.get(index);
    }

    private void getFullName(int index, String string) {
        YouTubeAPIHelper youTubeAPIHelper = new YouTubeAPIHelper(context,
                context.getResources().getString(R.string.GoogleMapsKey));
        String youTubeURL = getYouTubeID(string);
        if (youTubeURL != null && youTubeURL.contains("spacex/live")) {
            MaterialSimpleListItem item = new MaterialSimpleListItem.Builder(context)
                    .content("YouTube - SpaceX Livestream")
                    .build();
            update(index, item);
        } else {
            youTubeAPIHelper.getVideoById(youTubeURL, new retrofit2.Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Video> videos = response.body().getVideos();
                        if (videos.size() > 0) {
                            try {
                                MaterialSimpleListItem item = new MaterialSimpleListItem.Builder(context)
                                        .content(videos.get(0).getSnippet().getTitle())
                                        .build();
                                update(index, item);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                }
            });
        }
    }

    private String getYouTubeID(String vidURL) {
        final String regex = "(youtu\\.be\\/|youtube\\.com\\/(watch\\?(.*&)?v=|(embed|v)\\/|c\\/))([a-zA-Z0-9_-]{11}|[a-zA-Z].*)";
        final Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(vidURL);
        Timber.v("Checking for match of %s", vidURL);
        if (matcher.find() && (matcher.group(1) != null || matcher.group(2) != null) && matcher.group(5) != null) {
            return matcher.group(5);
        }
        return null;
    }

    public void update(int index, MaterialSimpleListItem item) {
        mItems.set(index, item);
        notifyDataSetChanged();
    }

    @Override
    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public SimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.md_simplelist_item, parent, false);
        return new SimpleListVH(view, this);
    }

    @Override
    public void onBindViewHolder(SimpleListVH holder, int position) {
        if (dialog != null) {
            final MaterialSimpleListItem item = mItems.get(position);
            if (item.getIcon() != null) {
                holder.icon.setImageDrawable(item.getIcon());
                holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(),
                        item.getIconPadding(), item.getIconPadding());
                holder.icon.getBackground().setColorFilter(item.getBackgroundColor(),
                        PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.icon.setVisibility(View.GONE);
            }
            holder.title.setTextColor(dialog.getBuilder().getItemColor());
            holder.title.setText(item.getContent());
            dialog.setTypeface(holder.title, dialog.getBuilder().getRegularFont());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final ImageView icon;
        final TextView title;
        final DialogAdapter adapter;

        public SimpleListVH(View itemView, DialogAdapter adapter) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(android.R.id.icon);
            title = (TextView) itemView.findViewById(android.R.id.title);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.mCallback != null)
                adapter.mCallback.onListItemSelected(getAdapterPosition(), adapter.getItem(getAdapterPosition()), false);
        }

        @Override
        public boolean onLongClick(View view) {
            if (adapter.mCallback != null)
                adapter.mCallback.onListItemSelected(getAdapterPosition(), adapter.getItem(getAdapterPosition()), true);
            return true;
        }
    }
}
