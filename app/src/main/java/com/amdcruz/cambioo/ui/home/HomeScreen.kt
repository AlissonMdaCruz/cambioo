package com.amdcruz.cambioo.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.components.EmptyPanel
import com.amdcruz.cambioo.ui.components.ErrorPanel
import com.amdcruz.cambioo.ui.components.LoadingPanel
import com.amdcruz.cambioo.ui.components.variationColor
import com.amdcruz.cambioo.ui.theme.TextLow
import com.amdcruz.cambioo.ui.theme.TextMedium

@Composable
fun HomeRoute(
    onQuoteClick: (currencyCode: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onQuoteClick = onQuoteClick,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onQuoteClick: (currencyCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        when (val state = uiState) {
            HomeUiState.Loading -> LoadingPanel(modifier = Modifier.padding(innerPadding))
            HomeUiState.Empty -> EmptyPanel(
                onRetry = onRefresh,
                modifier = Modifier.padding(innerPadding),
            )
            HomeUiState.Error -> ErrorPanel(
                onRetry = onRefresh,
                modifier = Modifier.padding(innerPadding),
            )
            is HomeUiState.Content -> HomeContent(
                state = state,
                onRefresh = onRefresh,
                onQuoteClick = onQuoteClick,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState.Content,
    onRefresh: () -> Unit,
    onQuoteClick: (currencyCode: String) -> Unit,
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
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.quote_date_label, state.latestQuoteDateText),
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
                state.notice?.let { notice ->
                    Text(
                        text = notice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            items(
                items = state.quotes,
                key = { quote -> quote.currencyCode },
            ) { quote ->
                QuoteRow(
                    quote = quote,
                    onClick = { onQuoteClick(quote.currencyCode) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun QuoteRow(
    quote: QuoteUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp)
            .semantics {
                contentDescription = buildString {
                    append(quote.valueSpoken)
                    quote.variationSpoken?.let { variation ->
                        append(", ")
                        append(variation)
                    }
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quote.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = quote.currencyCode,
                style = MaterialTheme.typography.bodySmall,
                color = TextMedium,
            )
            Text(
                text = quote.observationDateText,
                style = MaterialTheme.typography.bodySmall,
                color = TextLow,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = quote.valueVisual,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = quote.variationVisual,
                style = MaterialTheme.typography.labelMedium,
                color = variationColor(quote.variationDirection),
            )
        }
    }
}
