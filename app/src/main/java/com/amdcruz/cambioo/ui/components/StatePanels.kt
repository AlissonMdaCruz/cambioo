package com.amdcruz.cambioo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.theme.CambiooDimens

@Composable
fun LoadingPanel(
    modifier: Modifier = Modifier,
) {
    val loadingText = stringResource(R.string.loading_quotes)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CambiooDimens.PanelPadding)
            .semantics(mergeDescendants = true) {
                contentDescription = loadingText
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(CambiooDimens.SectionGap))
        Text(
            text = loadingText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyPanel(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessagePanel(
        message = stringResource(R.string.empty_quotes),
        icon = Icons.Outlined.Inbox,
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        onRetry = onRetry,
        modifier = modifier,
    )
}

@Composable
fun ErrorPanel(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessagePanel(
        message = stringResource(R.string.error_quotes),
        icon = Icons.Outlined.CloudOff,
        iconTint = MaterialTheme.colorScheme.error,
        onRetry = onRetry,
        modifier = modifier,
    )
}

@Composable
private fun MessagePanel(
    message: String,
    icon: ImageVector,
    iconTint: Color,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val retryLabel = stringResource(R.string.retry)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CambiooDimens.PanelPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(CambiooDimens.IconSize),
        )
        Spacer(modifier = Modifier.height(CambiooDimens.SectionGap))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics {
                liveRegion = LiveRegionMode.Assertive
            },
        )
        Spacer(modifier = Modifier.height(CambiooDimens.ScreenVertical))
        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.sizeIn(
                minWidth = CambiooDimens.MinTouchTarget,
                minHeight = CambiooDimens.MinTouchTarget,
            ),
        ) {
            Text(text = retryLabel)
        }
    }
}
