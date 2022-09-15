package com.example.squarednews.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.squarednews.Constants
import com.example.squarednews.NewsViewModel
import com.example.squarednews.R
import com.example.squarednews.data.Article
import com.example.squarednews.data.SearchResultState
import com.example.squarednews.domain.ParseDateStringUseCase
import com.example.squarednews.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NewsFeed(viewModel: NewsViewModel, newsHeadlines: LazyPagingItems<Article>, itemClickAction: (Article) -> Unit) {
    val isConnectedToNetwork = viewModel.isNetworkConnected.collectAsState()
    val searchResult = viewModel.searchResults.collectAsState()
    val selectedCountryCode = viewModel.selectedCountry.collectAsState("")
    var selectedCountry by remember { mutableStateOf("") }

    val sourcesList = viewModel.availableSources.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(false)
    val filtersSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var isCountryFilterSelected by remember { mutableStateOf(true) }

    val scrollState = rememberLazyListState()
    val filterListState = rememberLazyListState()

    LaunchedEffect(newsHeadlines.loadState) {
        refreshState.isRefreshing = newsHeadlines.loadState.mediator?.refresh is LoadState.Loading
    }

    ModalBottomSheetLayout(
        sheetContent = {
            LazyColumn(
                state = filterListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Box(
                        Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(Shapes.small)
                            .background(color = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isCountryFilterSelected)
                            stringResource(R.string.choose_location)
                        else
                            stringResource(R.string.filter_sources),
                        style = Typography.body1.copy(color = Color.DarkGray, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (isCountryFilterSelected) {
                    items(Constants.countries.toList()) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = it.second)
                            RadioButton(
                                selected = selectedCountry == it.first,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = primaryMain,
                                    unselectedColor = Color.LightGray
                                ),
                                onClick = { selectedCountry = it.first },
                                interactionSource = MutableInteractionSource()
                            )
                        }
                    }
                } else {
                    if (sourcesList.value.isEmpty()) {
                        item {
                            CircularProgressIndicator(color = primaryMain, strokeWidth = 4.dp)
                        }
                    } else {
                        items(sourcesList.value) { source ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = source, fontStyle = FontStyle.Italic, color = Color.Gray)
                                Checkbox(
                                    checked = source in viewModel.tempSelectedSources.value,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = primaryMain,
                                        uncheckedColor = Color.LightGray
                                    ),
                                    onCheckedChange = {
                                        viewModel.setSourceSelection(source, it)
                                    },
                                    interactionSource = MutableInteractionSource()
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item {
                    Button(
                        enabled = (isCountryFilterSelected && selectedCountry != selectedCountryCode.value)
                                || (viewModel.selectedSources.value.toList() != viewModel.tempSelectedSources.value.toList()),
                        onClick = {
                            coroutineScope.launch {
                                filtersSheetState.hide()
                                println("cond 1: ${selectedCountry != selectedCountryCode.value}")
                                println("cond 1: ${viewModel.selectedSources.value != viewModel.tempSelectedSources.value.toList()}")
                                scrollState.scrollToItem(0)
                            }
                            if (isCountryFilterSelected) {
                                viewModel.setSelectedCountry(selectedCountry)
                            } else {
                                viewModel.applySelectedSources()
                            }
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
                        Text(
                            text = if (isCountryFilterSelected) stringResource(R.string.apply)
                            else stringResource(R.string.apply_filter),
                            style = Typography.body2
                        )
                    }
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
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .clickable {
                                coroutineScope.launch { scrollState.animateScrollToItem(0) }
                            },
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
                                selectedCountry = selectedCountryCode.value
                                coroutineScope.launch { filtersSheetState.show() }
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
                        if (sourcesList.value.isEmpty()) {
                            viewModel.getAllNewsSources()
                        } else {
                            viewModel.tempSelectedSources.value = viewModel.selectedSources.value
                        }
                        coroutineScope.launch {
                            filterListState.scrollToItem(0)
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
                swipeEnabled = viewModel.searchKeyword.value.isEmpty(),
                indicator = { swipeRefreshState: SwipeRefreshState, dp: Dp ->
                    SwipeRefreshIndicator(
                        state = swipeRefreshState,
                        refreshTriggerDistance = dp,
                        backgroundColor = primaryMain,
                        contentColor = Color.White
                    )
                },
                onRefresh = { newsHeadlines.refresh() },
            ) {
                BackHandler(filtersSheetState.isVisible) {
                    coroutineScope.launch { filtersSheetState.hide() }
                }
                if (isConnectedToNetwork.value || newsHeadlines.itemCount > 0 || searchResult.value is SearchResultState.Success) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(PaddingValues(horizontal = 16.dp)),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            BasicTextField(
                                value = viewModel.searchKeyword.value,
                                onValueChange = {
                                    viewModel.searchKeyword.value = it.also {
                                        viewModel.searchNews(it)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = secondaryAux, shape = Shapes.small),
                                singleLine = true
                            ) {
                                TextFieldDefaults.TextFieldDecorationBox(
                                    innerTextField = it,
                                    value = viewModel.searchKeyword.value,
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
                        if (viewModel.searchKeyword.value.isBlank() || searchResult.value == null) {
                            if (newsHeadlines.itemCount > 0) {
                                items(newsHeadlines, key = { it.id }) {
                                    it?.let { NewsItem(it, viewModel.parseDateStringUseCase, itemClickAction) }
                                }
                                item {
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        when {
                                            newsHeadlines.loadState.append is LoadState.Loading ->
                                                CircularProgressIndicator(
                                                    color = primaryMain,
                                                    strokeWidth = 4.dp
                                                )
                                            newsHeadlines.loadState.append is LoadState.Error ->
                                                apiErrorStateView { newsHeadlines.retry() }
                                            newsHeadlines.loadState.append.endOfPaginationReached ->
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
                            searchResult.value?.let { response ->
                                when (response) {
                                    SearchResultState.Loading -> item {
                                        Box(Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = primaryMain,
                                                strokeWidth = 4.dp
                                            )
                                        }
                                    }
                                    is SearchResultState.Error -> when (response.errorType) {
                                        SearchResultState.ErrorType.NETWORK_ERROR -> item {
                                            noConnectionStateView {
                                                viewModel.searchNews(viewModel.searchKeyword.value)
                                            }
                                        }
                                        SearchResultState.ErrorType.API_ERROR -> item {
                                            apiErrorStateView {
                                                viewModel.searchNews(viewModel.searchKeyword.value)
                                            }
                                        }
                                    }
                                    is SearchResultState.Success -> if (response.list.isNotEmpty()) {
                                        items(items = response.list, key = { it.id }) {
                                            NewsItem(it, viewModel.parseDateStringUseCase, itemClickAction)
                                        }
                                        item { Spacer(Modifier.height(16.dp)) }
                                    } else item {
                                        emptyStateView(stringResource(R.string.empty_search_results))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    noConnectionStateView { newsHeadlines.retry() }
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

@OptIn(ExperimentalUnitApi::class)
@Composable
fun noConnectionStateView(onRetryButtonClick: () -> Unit) {
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
            onClick = onRetryButtonClick,
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
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun apiErrorStateView(onRetryButtonClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.api_error_message),
            color = Color.DarkGray,
            fontWeight = FontWeight.Normal,
            fontSize = TextUnit(14f, TextUnitType.Sp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetryButtonClick,
            contentPadding = PaddingValues(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = primaryMain
            )
        ) {
            Text(
                text = stringResource(R.string.retry_text),
                style = Typography.caption,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewsItem(article: Article, parseDateStringUseCase: ParseDateStringUseCase, onClick: (Article) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick(article) }
            .height(120.dp),
        shape = Shapes.small,
        backgroundColor = Color.White,
        elevation = 4.dp
    ) {
        Row(Modifier.padding(8.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = article.cleanUrl ?: "",
                    fontStyle = FontStyle.Italic,
                    color = primaryAux.copy(alpha = 0.6f)
                )
                Text(text = article.title ?: "",
                    maxLines = 3,
                    color = primaryAux,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true
                )
                Text(text = article.publishedDate?.let { s ->
                    getTimeDifference(parseDateStringUseCase, s)?.let {
                        if (it.first == 0)
                            it.second
                        else
                            pluralStringResource(R.plurals.time_ago, it.first, it.first, it.second)
                    }
                } ?: "",
                    style = Typography.caption.copy(color = Color.Gray)
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

fun getTimeDifference(parseDateStringUseCase: ParseDateStringUseCase, dateTime: String): Pair<Int, String>? =
    parseDateStringUseCase(dateTime)?.time?.let {
        when (val diff = ((Date().time - it) / 60000).toInt()) {
            0 -> 0 to "Just now"
            in 1 until 60 -> diff to "min"
            in 60 until 1440 -> (diff / 60) to "hr"
            in 1440 until 10080 -> (diff / 1440) to "day"
            else -> (diff / 10080) to "week"
        }
    }

@Preview
@Composable
fun ItemPreview() {
    NewsItem(
        Article(
            title = "Lorem ipsum dolor sit amet Lorem fsdffsdffv erfgrgfr rgregr rgrgreregreg rgregregerreregre regregerrere gergregre frgregreggrgregregr rreggge thghigheri igehjgi ierigreig",
            cleanUrl = "NewsSource",
            id = "grehg5654gterg",
            publishedDate = "2022-08-25 17:43:00"
        ),
        ParseDateStringUseCase()
    ) {}
}

@Preview
@Composable
fun emptyStatePreview() {
    emptyStateView(displayText = stringResource(R.string.empty_news_feed))
}