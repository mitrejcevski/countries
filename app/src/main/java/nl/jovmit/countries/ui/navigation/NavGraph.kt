package nl.jovmit.countries.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import nl.jovmit.countries.ui.screen.DetailsScreen
import nl.jovmit.countries.ui.screen.HomeScreen

@Composable
fun App() {
    val backStack = remember {
        mutableStateListOf<Screen>(Screen.Home)
    }

    when (val currentScreen = backStack.last()) {
        is Screen.Home ->
            HomeScreen(onClick = {
                backStack.add(Screen.Details(it))
            })

        is Screen.Details ->
            DetailsScreen(
                name = currentScreen.countryName,
                onBack = {
                    backStack.remove(currentScreen)
                }
            )
    }
}