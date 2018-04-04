package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.content.Context;
import android.os.Bundle;
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


import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.content.repository.ArticleRepository;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;

public class WebNewsFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private ArticleRepository articleRepository;
    private ArticleAdapter articleAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RealmResults<Article> articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        articleRepository = new ArticleRepository(context);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);


        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        articleAdapter = new ArticleAdapter(context);
        recyclerView.setAdapter(articleAdapter);
        articleRepository.getArticles(false, new ArticleRepository.GetArticlesCallback() {
            @Override
            public void onSuccess(RealmResults<Article> newArticles) {
                articles = newArticles;
                articleAdapter.addItems(articles);
                articles.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Article>>() {
                    @Override
                    public void onChange(RealmResults<Article> articles, OrderedCollectionChangeSet changeSet) {
                        articleAdapter.updateItems(changeSet);
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onNetworkFailure() {

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        articles.removeAllChangeListeners();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);

        if (SupporterHelper.isSupporter()) {
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
}
