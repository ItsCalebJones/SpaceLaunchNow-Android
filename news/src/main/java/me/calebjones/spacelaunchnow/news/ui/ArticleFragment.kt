/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.calebjones.spacelaunchnow.news.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.calebjones.spacelaunchnow.common.GlideApp
import me.calebjones.spacelaunchnow.news.R
import me.calebjones.spacelaunchnow.news.ServiceLocator
import me.calebjones.spacelaunchnow.news.repository.NetworkState
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import kotlinx.android.synthetic.main.activity_reddit.*

/**
 * A list activity that shows reddit posts in the given sub-reddit.
 * <p>
 * The intent arguments can be modified to make it use a different repository (see MainActivity).
 */
class ArticleFragment : Fragment() {

    private lateinit var model: ArticleViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_reddit, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
        model.showArticle()
        super.onViewCreated(view, savedInstanceState)
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
        val adapter = PostsAdapter(glide) {
            model.retry()
        }
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
