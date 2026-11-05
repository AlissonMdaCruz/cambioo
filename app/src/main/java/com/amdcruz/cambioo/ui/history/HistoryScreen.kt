package com.amdcruz.cambioo.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.common.AccessibilityAnnouncements
import com.amdcruz.cambioo.ui.components.EmptyPanel
import com.amdcruz.cambioo.ui.components.ErrorPanel
import com.amdcruz.cambioo.ui.components.LoadingPanel
import com.amdcruz.cambioo.ui.components.variationColor
import com.amdcruz.cambioo.ui.theme.TextLow
import com.amdcruz.cambioo.ui.theme.TextMedium

@Composable
fun HistoryRoute(
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
    )
}

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
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
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.history_title, state.displayName),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.currencyCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMedium,
                )
                Text(
                    text = stringResource(R.string.saved_at_label, state.savedAtText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMedium,
                )
                Text(
                    text = stringResource(R.string.source_label, state.sourceName),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLow,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                )
                if (state.notice != null) {
                    Text(
                        text = stringResource(R.string.notice_stale_quotes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            items(
                items = state.entries,
                key = { entry -> entry.observationDateText + entry.valueVisual },
            ) { entry ->
                HistoryRow(entry = entry)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 48.dp)
            .padding(vertical = 14.dp)
            .clearAndSetSemantics {
                contentDescription = announcement
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.observationDateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = entry.valueVisual,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = entry.variationVisual,
                style = MaterialTheme.typography.labelMedium,
                color = variationColor(entry.variationDirection),
            )
        }
    }
}
