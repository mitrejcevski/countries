package nl.jovmit.countries.ui.navigation

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Detail : Screen("detail")
}