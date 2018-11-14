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

package me.calebjones.spacelaunchnow.news.repository.inMemory.byPage

import androidx.lifecycle.Transformations
import androidx.annotation.MainThread
import androidx.paging.toLiveData
import me.calebjones.spacelaunchnow.news.api.NewsApi
import me.calebjones.spacelaunchnow.news.repository.Listing
import me.calebjones.spacelaunchnow.news.repository.NewsPostRepository
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import java.util.concurrent.Executor

/**
 * Repository implementation that returns a Listing that loads data directly from network by using
 * the previous / next page keys returned in the query.
 */
class InMemoryByPageKeyRepository(private val NewsApi: NewsApi,
                                  private val networkExecutor: Executor) : NewsPostRepository {
    @MainThread
    override fun postsOfsubreddit(subreddit: String, pageSize: Int): Listing<NewsArticle> {
        val sourceFactory = subredditDataSourceFactory(NewsApi, subreddit, networkExecutor)

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = sourceFactory.toLiveData(
                pageSize = pageSize,
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                fetchExecutor = networkExecutor)

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                  it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }
}

