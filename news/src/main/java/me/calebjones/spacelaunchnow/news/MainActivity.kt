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
package me.calebjones.spacelaunchnow.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.calebjones.spacelaunchnow.news.repository.NewsPostRepository
import me.calebjones.spacelaunchnow.news.ui.NewsActivity
import kotlinx.android.synthetic.main.activity_news_main.*
import me.calebjones.spacelaunchnow.news.R

/**
 * chooser activity for the demo.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_main)
        withDatabase.setOnClickListener {
            show(NewsPostRepository.Type.DB)
        }
        networkOnly.setOnClickListener {
            show(NewsPostRepository.Type.IN_MEMORY_BY_ITEM)
        }
        networkOnlyWithPageKeys.setOnClickListener {
            show(NewsPostRepository.Type.IN_MEMORY_BY_PAGE)
        }
    }

    private fun show(type: NewsPostRepository.Type) {
        val intent = NewsActivity.intentFor(this, type)
        startActivity(intent)
    }
}
