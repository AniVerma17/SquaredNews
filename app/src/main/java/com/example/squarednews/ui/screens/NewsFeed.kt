package com.example.squarednews.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.squarednews.Constants
import com.example.squarednews.NewsViewModel
import com.example.squarednews.R
import com.example.squarednews.data.Article
import com.example.squarednews.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalUnitApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NewsFeed(viewModel: NewsViewModel, itemClickAction: (Article) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val isConnectedToNetwork by remember { mutableStateOf(true) }
    val newsHeadlines: LazyPagingItems<Article> = viewModel.newsHeadlines.collectAsLazyPagingItems()
    val searchResult = viewModel.searchResults.collectAsState()
    val selectedCountryCode = viewModel.selectedCountry.collectAsState("")
    val allSources = viewModel.availableSources.collectAsState()

    val sourcesList = viewModel.availableSources.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(newsHeadlines.loadState.refresh is LoadState.Loading)
    val filtersSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var isCountryFilterSelected by remember { mutableStateOf(true) }

    ModalBottomSheetLayout(
        sheetContent = {
            var selectedCountry by remember { mutableStateOf("IN") }
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(state = rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                Box(
                    Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(Shapes.small)
                        .background(color = Color.LightGray)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isCountryFilterSelected) stringResource(R.string.choose_location) else stringResource(R.string.filter_sources),
                    style = Typography.body1,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                if (isCountryFilterSelected) {
                    Constants.countries.forEach {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = it.value)
                            RadioButton(
                                selected = selectedCountry == it.key,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = primaryMain,
                                    unselectedColor = Color.LightGray
                                ),
                                onClick = { selectedCountry = it.key },
                                interactionSource = MutableInteractionSource()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch { filtersSheetState.hide() }
                        viewModel.setSelectedCountry(selectedCountry)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = primaryMain,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                ) {
                    Text(text = if (isCountryFilterSelected) stringResource(R.string.apply) else stringResource(R.string.apply_filter),
                        style = Typography.body2
                    )
                }
            }
        },
        sheetState = filtersSheetState,
        scrimColor = primaryAux.copy(alpha = 0.4f),
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Scaffold(
            backgroundColor = secondaryMain,
            topBar = {
                TopAppBar(
                    backgroundColor = primaryMain,
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    Text(text = stringResource(R.string.app_name),
                        modifier = Modifier.align(Alignment.Bottom),
                        style = Typography.h6,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Column(modifier = Modifier.align(Alignment.Bottom),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.location),
                            style = Typography.caption, color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                isCountryFilterSelected = true
                                coroutineScope.launch {
                                    filtersSheetState.show()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Filled.LocationOn,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Text(
                                text = Constants.countries[selectedCountryCode.value] ?: "",
                                style = Typography.body2, color = Color.LightGray
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        isCountryFilterSelected = false
                        coroutineScope.launch {
                            filtersSheetState.show()
                        }
                    },
                    backgroundColor = primaryMain,
                    contentColor = Color.White
                ) {
                    Icon(painter = painterResource(R.drawable.ic_filter),
                        contentDescription = ""
                    )
                }
            }
        ) {
            SwipeRefresh(
                state = refreshState,
                swipeEnabled = searchQuery.isEmpty(),
                onRefresh = { newsHeadlines.refresh() },
            ) {
                LaunchedEffect(selectedCountryCode, sourcesList) {
                }
                if(!isConnectedToNetwork) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_no_connection),
                            modifier = Modifier
                                .width(128.dp)
                                .aspectRatio(1.5f),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.no_internet),
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Normal,
                            fontSize = TextUnit(16f, TextUnitType.Sp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refreshNewsFeed() },
                            contentPadding = PaddingValues(horizontal = 32.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = primaryMain)
                        ) {
                            Text(
                                text = stringResource(R.string.retry_text),
                                style = Typography.subtitle2,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(PaddingValues(horizontal = 24.dp, vertical = 16.dp)),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    if (searchQuery.length >= 3) {
                                        viewModel.searchNews(searchQuery)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = secondaryAux, shape = Shapes.small),
                                singleLine = true
                            ) {
                                TextFieldDefaults.TextFieldDecorationBox(
                                    innerTextField = it,
                                    value = searchQuery,
                                    placeholder = {
                                        Text(
                                            text = stringResource(R.string.search_placeholder),
                                            style = Typography.caption.copy(color = Color.Gray)
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Search,
                                            contentDescription = ""
                                        )
                                    },
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                            }
                        }
                        if (searchQuery.isBlank()) {
                            if (newsHeadlines.itemCount > 0) {
                                items(newsHeadlines, key = { it.id }) {
                                    it?.let { NewsItem(it, itemClickAction) }
                                }
                                item {
                                    when {
                                        newsHeadlines.loadState.append is LoadState.Loading -> {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = primaryMain,
                                                    strokeWidth = 4.dp
                                                )
                                            }
                                        }
                                        newsHeadlines.loadState.append is LoadState.Error -> {
                                            /*CircularProgressIndicator(
                                                Modifier.padding(8.dp),
                                                color = primaryMain,
                                                strokeWidth = 4.dp
                                            )*/
                                        }
                                        newsHeadlines.loadState.append.endOfPaginationReached -> {
                                            Text(
                                                text = stringResource(R.string.end_of_feed),
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(24.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                item { emptyStateView(stringResource(R.string.empty_news_feed)) }
                            }
                        } else {
                            if (searchResult.value.isNotEmpty())
                                items(items = searchResult.value, key = { it.id }) {
                                    NewsItem(it, itemClickAction)
                                }
                            else
                                item { emptyStateView(stringResource(R.string.empty_search_results)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun emptyStateView(displayText: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = displayText,
            Modifier.padding(24.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NewsItem(article: Article, onClick: (Article) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick(article) }
            .height(120.dp),
        shape = Shapes.small,
        backgroundColor = Color.White,
        elevation = 8.dp
    ) {
        Row(Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = article.cleanUrl ?: "")
                Text(text = article.title ?: "",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true
                )
                Text(text = article.publishedDate?.let { getTimeAgoText(it) } ?: "",
                    style = Typography.caption
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            AsyncImage(model = article.media,
                contentDescription = "",
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(Shapes.small)
            )
        }
    }
}

fun getTimeAgoText(dateTime: String): String? = SimpleDateFormat(Constants.DATE_PATTERN).apply {
    timeZone = TimeZone.getTimeZone("IST")
}.parse(dateTime)?.time?.let {
    when (val diff = (Date().time - it) / 60000) {
        0L -> "Just now"
        in 1 until 60 -> "$diff min ago"
        in 60 until 1440 -> "${diff / 60} hrs ago"
        in 1440 until 10080 -> "${diff / 1440} days ago"
        else -> "${diff / 10080} weeks ago"
    }
}

@Preview
@Composable
fun ItemPreview() {
    NewsItem(
        Article(
            title = "Lorem ipsum dolor sit amet Lorem fsdffsdffv erfgrgfr rgregr rgrgreregreg rgregregerreregre regregerrere gergregre frgregreggrgregregr rreggge thghigheri igehjgi ierigreig",
            link = "NewsSource",
            id = "grehg5654gterg"
        )
    ) {}
}

@Preview
@Composable
fun emptyStatePreview() {
    emptyStateView(displayText = stringResource(R.string.empty_news_feed))
}