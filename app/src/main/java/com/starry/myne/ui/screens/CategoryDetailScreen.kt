package com.starry.myne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.starry.myne.R
import com.starry.myne.navigation.Screens
import com.starry.myne.others.NetworkObserver
import com.starry.myne.others.viewModelFactory
import com.starry.myne.ui.common.BookItemCard
import com.starry.myne.ui.common.ProgressDots
import com.starry.myne.ui.theme.pacificoFont
import com.starry.myne.ui.viewmodels.CategoryViewModel
import com.starry.myne.utils.BookUtils
import kotlinx.coroutines.delay

@ExperimentalCoilApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun CategoryDetailScreen(
    category: String,
    navController: NavController,
    networkStatus: NetworkObserver.Status
) {
    if (networkStatus == NetworkObserver.Status.Available) {
        val viewModel = viewModel<CategoryViewModel>(factory = viewModelFactory {
            CategoryViewModel(category)
        })
        val state = viewModel.state

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CategoryDetailTopBar(
                category = category,
                onBackClicked = { navController.navigateUp() }
            )
            if (state.page == 1L && state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    items(state.items.size) { i ->
                        val item = state.items[i]
                        if (i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                            viewModel.loadNextItems()
                        }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            BookItemCard(
                                title = item.title,
                                author = BookUtils.getAuthorsAsString(item.authors),
                                language = BookUtils.getLanguagesAsString(item.languages),
                                subjects = BookUtils.getSubjectsAsString(item.subjects, 3),
                                coverImageUrl = item.formats.imagejpeg
                            ) {
                                navController.navigate(Screens.BookDetailScreen.withBookId(item.id.toString()))
                            }
                        }

                    }
                    item {
                        if (state.isLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ProgressDots()
                            }
                        }
                    }
                }
            }

        }

    } else {
        var showNoInternet by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = Unit) {
            delay(250)
            showNoInternet = true
        }
        if (showNoInternet) {
            NoInternetScreen()
        }
    }
}


@Composable
fun CategoryDetailTopBar(
    category: String,
    onBackClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(start = 18.dp, top = 18.dp, bottom = 18.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                .clickable { onBackClicked() }
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = stringResource(id = R.string.back_button_desc),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = category,
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
            fontFamily = pacificoFont,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.weight(1.56f))
    }
}
