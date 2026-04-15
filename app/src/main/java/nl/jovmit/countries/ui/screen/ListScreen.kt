package nl.jovmit.countries.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.jovmit.countries.ui.viewmodel.CountriesListUiState
import nl.jovmit.countries.ui.viewmodel.CountriesViewModel
import nl.jovmit.countries.data.model.Country

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: CountriesViewModel,
    onClick: (String) -> Unit = {},
    onClickFavorite: (String) -> Unit = {}
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCountries()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        when (val uiState = state) {

            is CountriesListUiState.Countries -> {
                val list = uiState.countries

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(list, key = { it.name }) { country ->
                        CountryCard(
                            country,
                            onClick,
                            onClickFavorite
                        )
                    }
                }
            }

            is CountriesListUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Something went wrong")
                }
            }
        }
    }
}

@Composable
fun CountryCard(
    country: Country,
    onClick: (String) -> Unit,
    onClickFavorite: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(country.name) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = country.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Capital: ${country.capital}")
            Text("Region: ${country.language}")
            Text("Population: ${country.population}")
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = if (country.isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                modifier = Modifier.clickable {
                    onClickFavorite(country.name)
                }
            )
        }
    }
}