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

package me.calebjones.spacelaunchnow.news.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import me.calebjones.spacelaunchnow.news.vo.NewsArticle

/**
 * Database schema used by the DbNewsPostRepository
 */
@Database(
        entities = arrayOf(NewsArticle::class),
        version = 1,
        exportSchema = false
)

abstract class NewsDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory : Boolean): NewsDb {
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, NewsDb::class.java)
            } else {
                Room.databaseBuilder(context, NewsDb::class.java, "news.db")
            }
            return databaseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun posts(): NewsPostDao
}