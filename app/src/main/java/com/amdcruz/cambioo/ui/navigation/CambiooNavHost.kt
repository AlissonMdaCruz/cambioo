package com.amdcruz.cambioo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amdcruz.cambioo.ui.history.HistoryRoute
import com.amdcruz.cambioo.ui.history.HistoryViewModel
import com.amdcruz.cambioo.ui.home.HomeRoute

object CambiooDestinations {
    const val HOME: String = "home"
    const val HISTORY: String = "history/{${HistoryViewModel.CURRENCY_CODE_KEY}}"

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
                navArgument(HistoryViewModel.CURRENCY_CODE_KEY) { type = NavType.StringType },
            ),
        ) {
            HistoryRoute()
        }
    }
}
