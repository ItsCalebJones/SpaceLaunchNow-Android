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

package me.calebjones.spacelaunchnow.news.ui.articles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.calebjones.spacelaunchnow.common.GlideRequests
import me.calebjones.spacelaunchnow.news.R
import me.calebjones.spacelaunchnow.news.vo.NewsArticle
import java.text.SimpleDateFormat
import java.util.*

/**
 * A RecyclerView ViewHolder that displays a reddit post.
 */
class ArticlePostViewHolder(view: View, private val glide: GlideRequests, private val context: Context?)
    : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.article_title)
    private val subtitle: TextView = view.findViewById(R.id.article_site)
    private val date: TextView = view.findViewById(R.id.article_publication_date)
    private val thumbnail: ImageView = view.findViewById(R.id.article_image)
    private val format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM d, yyyy")
    private val outDateFormat: SimpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    private var post: NewsArticle? = null

    init {
        view.setOnClickListener {
            post?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(post: NewsArticle?) {
        this.post = post
        title.text = post?.title ?: "loading"
        subtitle.text = itemView.context.resources.getString(R.string.post_subtitle,
                post?.news_site_long ?: "unknown")
        date.text = outDateFormat.format(post?.date_published)
        when {
            post?.featured_image?.startsWith("http") == true -> {
                thumbnail.visibility = View.VISIBLE
                glide.load(post.featured_image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_insert_photo_black_48dp)
                        .into(thumbnail)
            }
            post?.news_site != null -> tryDefault(post.news_site, thumbnail)
            else -> glide.clear(thumbnail)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests, context: Context?): ArticlePostViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.article_item, parent, false)
            return ArticlePostViewHolder(view, glide, context)
        }
    }

    private fun tryDefault(link: String, imageView: ImageView) {
        when {
            link.contains("spaceflightnow") -> glide.load(context?.getString(R.string.spaceflightnow_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView)
            link.contains("spaceflight101") -> glide.load(context?.getString(R.string.spaceflight_101))
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
            link.contains("spacenews") -> glide.load(context?.getString(R.string.spacenews_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView)
            link.contains("nasaspaceflight") -> glide.load(context?.getString(R.string.nasaspaceflight_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView)
            link.contains("nasa.gov") -> glide.load(context?.getString(R.string.NASA_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .into(imageView)
            link.contains("spacex.com") -> glide.load(context?.getString(R.string.spacex_logo))
                    .centerInside()
                    .placeholder(R.drawable.placeholder).centerInside()
                    .fitCenter()
                    .into(imageView)
            else -> glide.load(R.drawable.placeholder)
                    .centerCrop()
                    .into(imageView)
        }
    }

    fun updateScore(item: NewsArticle?) {
        post = item
        date.text = "${item?.date_published ?: 0}"
    }
}