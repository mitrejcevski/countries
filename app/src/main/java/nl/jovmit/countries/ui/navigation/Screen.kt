package nl.jovmit.countries.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    data object Home : Screen
    data class Details(val countryName: String) : Screen
}
