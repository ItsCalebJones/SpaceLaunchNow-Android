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
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import me.calebjones.spacelaunchnow.news.ServiceLocator
import me.calebjones.spacelaunchnow.news.repository.NetworkState
import me.calebjones.spacelaunchnow.news.repository.NewsPostRepository
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import kotlinx.android.synthetic.main.activity_reddit.*
import me.calebjones.spacelaunchnow.common.GlideApp
import me.calebjones.spacelaunchnow.news.R

/**
 * A list activity that shows NewsArticle posts in the given sub-NewsArticle.
 * <p>
 * The intent arguments can be modified to make it use a different repository (see MainActivity).
 */
class NewsActivity : AppCompatActivity() {
    companion object {
        const val KEY_subreddit = "subreddit"
        const val DEFAULT_subreddit = "androiddev"
        const val KEY_REPOSITORY_TYPE = "repository_type"
        fun intentFor(context: Context, type: NewsPostRepository.Type): Intent {
            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra(KEY_REPOSITORY_TYPE, type.ordinal)
            return intent
        }
    }

    private lateinit var model: subredditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit)
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
        initSearch()
        val subreddit = savedInstanceState?.getString(KEY_subreddit) ?: DEFAULT_subreddit
        model.showsubreddit(subreddit)
    }

    private fun getViewModel(): subredditViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repoTypeParam = intent.getIntExtra(KEY_REPOSITORY_TYPE, 0)
                val repoType = NewsPostRepository.Type.values()[repoTypeParam]
                val repo = ServiceLocator.instance(this@NewsActivity)
                        .getRepository(repoType)
                @Suppress("UNCHECKED_CAST")
                return subredditViewModel(repo) as T
            }
        })[subredditViewModel::class.java]
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
        outState.putString(KEY_subreddit, model.currentsubreddit())
    }

    private fun initSearch() {
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedsubredditFromInput()
                true
            } else {
                false
            }
        }
        input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedsubredditFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedsubredditFromInput() {
        input.text.trim().toString().let {
            if (it.isNotEmpty()) {
                if (model.showsubreddit(it)) {
                    list.scrollToPosition(0)
                    (list.adapter as? PostsAdapter)?.submitList(null)
                }
            }
        }
    }
}
