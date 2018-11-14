package me.calebjones.spacelaunchnow.news.ui.news.web;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.data.articles.ArticleRepository;
import me.calebjones.spacelaunchnow.data.models.news.Article;
import me.calebjones.spacelaunchnow.local.common.RetroFitFragment;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.views.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import timber.log.Timber;

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
    private List<Article> articles;
    private int page = 1;
    private boolean canLoadMore = true;
    private boolean statefulStateContentShow = false;
    private EndlessRecyclerViewScrollListener scrollListener;
    Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        articleRepository = new ArticleRepository(context, getNewsRetrofit());
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        unbinder = ButterKnife.bind(this, view);
        articleAdapter = new ArticleAdapter(context, getActivity());
        linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    getNextPage();
                }
            }
        };
        recyclerView.setAdapter(articleAdapter);
        getFirstPage();
        swipeRefreshLayout.setOnRefreshListener(() -> getFirstPage());
        statefulView.showProgress();
        statefulView.setOfflineRetryOnClickListener(v -> getArticles(true, page));

        recyclerView.addOnScrollListener(scrollListener);
        return view;
    }

    private void getFirstPage() {
        page = 1;
        getArticles(true, page);
    }

    private void getNextPage() {
        page = page + 1;
        getArticles(true, page);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
        swipeRefreshLayout.setOnRefreshListener(null);
        unbinder.unbind();
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

    public void getArticles(boolean forced, int page) {
        swipeRefreshLayout.setRefreshing(true);
        articleRepository.getArticles(forced, page, new ArticleRepository.GetArticlesCallback() {
            @Override
            public void onSuccess(List<Article> newArticles) {
                try {
                    articles = newArticles;
                    if (!statefulStateContentShow) {
                        statefulView.showContent();
                        statefulStateContentShow = true;
                    }
                    articleAdapter.addItems(articles);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    Timber.e(e);
                }
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
                statefulStateContentShow = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onLastPageLoaded() {
                canLoadMore = false;
            }
        });
    }
}
