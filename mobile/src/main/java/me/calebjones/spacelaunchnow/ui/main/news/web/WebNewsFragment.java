package me.calebjones.spacelaunchnow.ui.main.news.web;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.RetroFitFragment;
import me.calebjones.spacelaunchnow.content.data.articles.ArticleRepository;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;

public class WebNewsFragment extends RetroFitFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private Context context;
    private ArticleRepository articleRepository;
    private ArticleAdapter articleAdapter;
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager layoutManager;
    private List<Article> articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        articleRepository = new ArticleRepository(context, getNewsRetrofit());
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        articleAdapter = new ArticleAdapter(context, getActivity());
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
        recyclerView.setAdapter(articleAdapter);
        getArticles(false);
        swipeRefreshLayout.setOnRefreshListener(() -> getArticles(true));
        statefulView.showProgress();
        statefulView.setOfflineRetryOnClickListener(v -> getArticles(true));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public void getArticles(boolean forced) {
        swipeRefreshLayout.setRefreshing(true);
        articleRepository.getArticles(forced, new ArticleRepository.GetArticlesCallback() {
            @Override
            public void onSuccess(List<Article> newArticles) {
                articles = newArticles;
                statefulView.showContent();
                articleAdapter.addItems(articles);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String error, boolean showContent) {
                if (!showContent) {
                    statefulView.showEmpty();
                }
                SnackbarHandler.showInfoSnackbar(context, coordinatorLayout, error);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNetworkFailure() {
                statefulView.showOffline();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
