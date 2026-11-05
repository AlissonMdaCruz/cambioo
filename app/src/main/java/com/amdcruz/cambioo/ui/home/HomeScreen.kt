package com.amdcruz.cambioo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
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
    val pullToRefreshState = rememberPullToRefreshState()
    val listDescription = stringResource(R.string.a11y_home_list)
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
                HomeHeader(state = state)
            }
            items(
                items = state.quotes,
                key = { quote -> quote.currencyCode },
            ) { quote ->
                QuoteRow(
                    quote = quote,
                    onClick = { onQuoteClick(quote.currencyCode) },
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    state: HomeUiState.Content,
) {
    Column {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(CambiooDimens.SectionGap))
        Text(
            text = stringResource(R.string.quote_date_label, state.latestQuoteDateText),
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
private fun QuoteRow(
    quote: QuoteUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openHistoryLabel = stringResource(R.string.a11y_open_history)
    val announcement = AccessibilityAnnouncements.quoteRow(
        valueSpoken = quote.valueSpoken,
        variationSpoken = quote.variationSpoken,
    )
    RateSurface(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = announcement
            role = Role.Button
            onClick(label = openHistoryLabel) {
                onClick()
                true
            }
        },
        onClick = onClick,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quote.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = quote.currencyCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = quote.observationDateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = quote.valueVisual,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            VariationChip(
                text = quote.variationVisual,
                direction = quote.variationDirection,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = CambiooDimens.ItemGap)
                .size(20.dp)
                .semantics { hideFromAccessibility() },
        )
    }
}
