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
package me.calebjones.spacelaunchnow.news.api


import android.util.Log
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API communication setup
 */
interface ArticleApi {
    @GET("/articles")
    fun getTop(
            @Query("limit") limit: Int): Call<List<NewsArticle>>


    @GET("/articles")
    fun getTopAfter(
            @Query("page") page: String,
            @Query("limit") limit: Int): Call<List<NewsArticle>>

    @GET("/articles")
    fun getTopBefore(
            @Query("limit") limit: Int): Call<List<NewsArticle>>

    companion object {
        private const val BASE_URL = "https://api.spaceflightnewsapi.net"
        public fun create(): ArticleApi = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): ArticleApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ArticleApi::class.java)
        }
    }
}