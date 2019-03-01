package me.calebjones.spacelaunchnow.news.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "article",
        indices = [Index(value = ["news_site"], unique = false)])
data class NewsArticle(
        @PrimaryKey
        @SerializedName("_id")
        val id: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("news_site_long")
        val news_site_long: String,
        @SerializedName("news_site")
        @ColumnInfo(collate = ColumnInfo.NOCASE)
        val news_site: String,
        @SerializedName("featured_image")
        val featured_image: String,
        @SerializedName("date_published")
        val date_published: Int,
        val url: String?) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}