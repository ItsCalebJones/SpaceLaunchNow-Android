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

package me.calebjones.spacelaunchnow.news.repository.inMemory.byItem

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import me.calebjones.spacelaunchnow.news.api.NewsApi
import me.calebjones.spacelaunchnow.news.repository.NetworkState
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

/**
 * A data source that uses the "name" field of posts as the key for next/prev pages.
 * <p>
 * Note that this is not the correct consumption of the NewsArticle API but rather shown here as an
 * alternative implementation which might be more suitable for your backend.
 * see PageKeyedsubredditDataSource for the other sample.
 */
class ItemKeyedsubredditDataSource(
        private val NewsApi: NewsApi,
        private val subredditName: String,
        private val retryExecutor: Executor)
    : ItemKeyedDataSource<String, NewsArticle>() {
    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter and we don't support loadBefore
     * in this example.
     * <p>
     * See BoundaryCallback example for a more complete example on syncing multiple network states.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()
    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<NewsArticle>) {
        // ignored, since we only ever append to our initial load
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<NewsArticle>) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING)
        // even though we are using async retrofit API here, we could also use sync
        // it is just different to show that the callback can be called async.
        NewsApi.getTopAfter(subreddit = subredditName,
                after = params.key,
                limit = params.requestedLoadSize).enqueue(
                object : retrofit2.Callback<NewsApi.ListingResponse> {
                    override fun onFailure(call: Call<NewsApi.ListingResponse>, t: Throwable) {
                        // keep a lambda for future retry
                        retry = {
                            loadAfter(params, callback)
                        }
                        // publish the error
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(
                            call: Call<NewsApi.ListingResponse>,
                            response: Response<NewsApi.ListingResponse>) {
                        if (response.isSuccessful) {
                            val items = response.body()?.data?.children?.map { it.data } ?: emptyList()
                            // clear retry since last request succeeded
                            retry = null
                            callback.onResult(items)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue(
                                    NetworkState.error("error code: ${response.code()}"))
                        }
                    }
                }
        )
    }

    /**
     * The name field is a unique identifier for post items.
     * (no it is not the title of the post :) )
     * https://www.NewsArticle.com/dev/api
     */
    override fun getKey(item: NewsArticle): String = item.name

    override fun loadInitial(
            params: LoadInitialParams<String>,
            callback: LoadInitialCallback<NewsArticle>) {
        val request = NewsApi.getTop(
                subreddit = subredditName,
                limit = params.requestedLoadSize
        )
        // update network states.
        // we also provide an initial load state to the listeners so that the UI can know when the
        // very first list is loaded.
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        // triggered by a refresh, we better execute sync
        try {
            val response = request.execute()
            val items = response.body()?.data?.children?.map { it.data } ?: emptyList()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }
}