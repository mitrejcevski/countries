package nl.jovmit.countries.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nl.jovmit.countries.ui.screen.DetailsScreen
import nl.jovmit.countries.ui.screen.ListScreen
import nl.jovmit.countries.ui.viewmodel.CountriesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = modifier
    ) {

        // LIST SCREEN
        composable("list") {
            val viewModel: CountriesViewModel = koinViewModel()
            ListScreen(
                viewModel = viewModel,
                onClick = { country ->
                    navController.navigate("detail/${country}")
                },
                onClickFavorite = { country ->
                    viewModel.toggleFavorite(country)
                }
            )
        }

        // DETAIL SCREEN
        composable(
            route = "detail/{countryName}",
            arguments = listOf(
                navArgument("countryName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val countryName =
                backStackEntry.arguments?.getString("countryName") ?: ""

            DetailsScreen(name = countryName)
        }
    }
}