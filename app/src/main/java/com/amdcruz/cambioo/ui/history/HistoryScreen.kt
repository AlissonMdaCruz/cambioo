package com.amdcruz.cambioo.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.common.AccessibilityAnnouncements
import com.amdcruz.cambioo.ui.components.EmptyPanel
import com.amdcruz.cambioo.ui.components.ErrorPanel
import com.amdcruz.cambioo.ui.components.LoadingPanel
import com.amdcruz.cambioo.ui.components.RateSurface
import com.amdcruz.cambioo.ui.components.StaleNoticeBanner
import com.amdcruz.cambioo.ui.components.VariationChip
import com.amdcruz.cambioo.ui.theme.CambiooDimens

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (uiState) {
        is HistoryUiState.Content -> uiState.displayName
        else -> stringResource(R.string.app_name)
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            HistoryUiState.Loading -> LoadingPanel(modifier = Modifier.padding(innerPadding))
            HistoryUiState.Empty -> EmptyPanel(
                onRetry = onRefresh,
                modifier = Modifier.padding(innerPadding),
            )
            HistoryUiState.Error -> ErrorPanel(
                onRetry = onRefresh,
                modifier = Modifier.padding(innerPadding),
            )
            is HistoryUiState.Content -> HistoryContent(
                state = state,
                onRefresh = onRefresh,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryContent(
    state: HistoryUiState.Content,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val listDescription = stringResource(R.string.a11y_history_list)
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = state.isRefreshing,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = listDescription },
            contentPadding = PaddingValues(
                horizontal = CambiooDimens.ScreenHorizontal,
                vertical = CambiooDimens.ScreenVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(CambiooDimens.ItemGap),
        ) {
            item {
                HistoryHeader(state = state)
            }
            items(
                items = state.entries,
                key = { entry -> entry.observationDateText + entry.valueVisual },
            ) { entry ->
                HistoryRow(entry = entry)
            }
        }
    }
}

@Composable
private fun HistoryHeader(
    state: HistoryUiState.Content,
) {
    Column {
        Text(
            text = state.currencyCode,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.saved_at_label, state.savedAtText),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.source_label, state.sourceName),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                top = CambiooDimens.MetaCaptionTop,
                bottom = CambiooDimens.ItemGap,
            ),
        )
        if (state.notice != null) {
            StaleNoticeBanner(
                modifier = Modifier.padding(bottom = CambiooDimens.ItemGap),
            )
        }
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntryUi,
    modifier: Modifier = Modifier,
) {
    val announcement = AccessibilityAnnouncements.historyRow(
        observationDateSpoken = entry.observationDateSpoken,
        valueSpoken = entry.valueSpoken,
        variationSpoken = entry.variationSpoken,
    )
    RateSurface(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = announcement
        },
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.observationDateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = entry.valueVisual,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            VariationChip(
                text = entry.variationVisual,
                direction = entry.variationDirection,
            )
        }
    }
}
