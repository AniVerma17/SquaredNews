package com.example.squarednews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.squarednews.data.Article
import com.example.squarednews.ui.screens.NewsDetail
import com.example.squarednews.ui.screens.NewsFeed
import com.example.squarednews.ui.theme.SquaredNewsTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SquaredNewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val newsHeadlines: LazyPagingItems<Article> = viewModel.newsHeadlines.collectAsLazyPagingItems()
                    NavHost(navController = navController, startDestination = "newsfeed") {
                        composable("newsfeed") {
                            NewsFeed(viewModel, newsHeadlines) {
                                viewModel.articleToDisplay = it
                                navController.navigate("news_detail")
                            }
                        }
                        composable("news_detail") {
                            NewsDetail(viewModel)
                        }
                    }
                }
            }
        }
    }
}