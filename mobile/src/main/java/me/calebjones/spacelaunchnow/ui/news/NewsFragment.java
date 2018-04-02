package me.calebjones.spacelaunchnow.ui.news;


import android.content.Context;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Article;
import me.calebjones.spacelaunchnow.content.models.NewsFeedResponse;
import me.calebjones.spacelaunchnow.content.network.NewsAPIClient;
import me.calebjones.spacelaunchnow.content.repository.ArticleRepository;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class NewsFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private LinearLayoutManager linearLayoutManager;
    private ArticleRepository articleRepository;
    private RealmResults<Article> articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        articleRepository = new ArticleRepository(context);

        final TwitterListTimeline timeline = new TwitterListTimeline.Builder()
                .slugWithOwnerScreenName("space-launch-news", "SpaceLaunchNow")
                .build();

        final TweetTimelineRecyclerViewAdapter adapter =
                new TweetTimelineRecyclerViewAdapter.Builder(context)
                        .setTimeline(timeline)
                        .setViewStyle(R.style.SpaceLaunchNowTweetStyle)
                        .build();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);

        articles = articleRepository.getArticles();
        articles.addChangeListener(callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);

        if(SupporterHelper.isSupporter()){
            menu.removeItem(R.id.action_supporter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private OrderedRealmCollectionChangeListener<RealmResults<Article>> callback = new OrderedRealmCollectionChangeListener<RealmResults<Article>>() {
        @Override
        public void onChange(RealmResults<Article> results, OrderedCollectionChangeSet changeSet) {
            if (changeSet == null) {
                // The first time async returns with an null changeSet.
            } else {
                // Called on every update.
            }
        }
    };

    @Override
    public void onStop () {
        super.onStop();
        articles.removeAllChangeListeners(); // remove all registered listeners
    }

}
