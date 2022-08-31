package com.example.squarednews.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.squarednews.Constants
import com.example.squarednews.NewsViewModel
import com.example.squarednews.R
import com.example.squarednews.data.Article
import com.example.squarednews.ui.theme.Typography
import com.example.squarednews.ui.theme.primaryAux
import com.example.squarednews.ui.theme.primaryMain
import com.example.squarednews.ui.theme.secondaryMain
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsDetail(viewModel: NewsViewModel) {
    val context = LocalContext.current
    newsDetailView(viewModel.articleToDisplay) {
        context.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(it.link)))
    }
}

@Composable
fun newsDetailView(article: Article, onShowStoryClicked: (Article) -> Unit) {
    val dateTime = article.publishedDate?.let { date ->
        SimpleDateFormat(Constants.DATE_PATTERN).apply {
            timeZone = TimeZone.getTimeZone("IST")
        }.parse(date)?.let {
            SimpleDateFormat("dd MMM, yyyy 'at' h:mm a").format(it)
        }
    } ?: "Unknown date"
    Scaffold(
        backgroundColor = secondaryMain,
        topBar = {
            TopAppBar(
                backgroundColor = primaryMain,
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {}
        }
    ) {
        Column(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.height(240.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(model = article.media,
                    contentDescription = "",
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = article.title ?: "No title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black)
                            )
                        )
                        .padding(16.dp),
                    style = Typography.body1.copy(color = Color.White),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = article.cleanUrl ?: "Unknown source",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = Typography.subtitle2.copy(fontStyle = FontStyle.Italic, color = primaryAux)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = dateTime,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = Typography.caption.copy(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = article.summary ?: "No description",
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { onShowStoryClicked(article) },
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = primaryMain),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.see_full_story),
                    style = Typography.subtitle1
                )
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "")
            }
        }
    }
}

@Preview
@Composable
fun newsDetailPreview() {
    newsDetailView(article = Article(
        title = "Lorem ipsum is too mainstream",
        cleanUrl = "Squareboat.com",
        id = "greg65teg34",
        summary = "No description",
        publishedDate = "2022-08-25 17:43:00"
    )) {}
}