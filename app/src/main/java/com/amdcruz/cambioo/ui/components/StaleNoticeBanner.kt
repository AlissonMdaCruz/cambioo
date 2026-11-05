package com.amdcruz.cambioo.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.theme.CambiooDimens

@Composable
fun StaleNoticeBanner(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.45f),
                shape = MaterialTheme.shapes.small,
            ),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = stringResource(R.string.notice_stale_quotes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(CambiooDimens.BannerPadding),
        )
    }
}
