package com.amdcruz.cambioo.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amdcruz.cambioo.R
import com.amdcruz.cambioo.ui.home.HomeRoute

object CambiooDestinations {
    const val HOME: String = "home"
    const val HISTORY: String = "history/{currencyCode}"

    fun history(currencyCode: String): String = "history/$currencyCode"
}

@Composable
fun CambiooNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = CambiooDestinations.HOME,
        modifier = modifier,
    ) {
        composable(route = CambiooDestinations.HOME) {
            HomeRoute(
                onQuoteClick = { currencyCode ->
                    navController.navigate(CambiooDestinations.history(currencyCode))
                },
            )
        }
        composable(
            route = CambiooDestinations.HISTORY,
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
            ),
        ) { entry ->
            val currencyCode = entry.arguments?.getString("currencyCode").orEmpty()
            HistoryPlaceholderScreen(
                currencyCode = currencyCode,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun HistoryPlaceholderScreen(
    currencyCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.history_placeholder, currencyCode),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 16.dp)
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        ) {
            Text(text = stringResource(R.string.back))
        }
    }
}
