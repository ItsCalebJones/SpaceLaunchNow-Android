package me.calebjones.spacelaunchnow.news.ui.articles

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import me.calebjones.spacelaunchnow.common.GlideApp
import me.calebjones.spacelaunchnow.news.R
import me.calebjones.spacelaunchnow.news.ServiceLocator
import me.calebjones.spacelaunchnow.news.repository.NetworkState
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import kotlinx.android.synthetic.main.fragment_reddit.*
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration


class ArticleFragment : Fragment() {

    private lateinit var model: ArticleViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reddit, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
        model.showArticle()
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        inflater!!.inflate(R.menu.main_menu, menu)
    }

    private fun getViewModel(): ArticleViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(activity?.applicationContext!!).getRepository()
                @Suppress("UNCHECKED_CAST")
                return ArticleViewModel(repo) as T
            }
        })[ArticleViewModel::class.java]
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = ArticleAdapter(glide, context) {
            model.retry()
        }
        val itemDecorator = SimpleDividerItemDecoration(context);
        list.addItemDecoration(itemDecorator)
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<NewsArticle>> {
            adapter.submitList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}
