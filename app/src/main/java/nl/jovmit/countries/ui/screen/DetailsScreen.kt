package nl.jovmit.countries.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.jovmit.countries.ui.viewmodel.CountryDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    name: String,
    viewModel: CountryDetailViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadCountryDetails(name)
    }
}