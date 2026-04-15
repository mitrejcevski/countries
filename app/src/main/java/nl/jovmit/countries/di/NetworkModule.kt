package nl.jovmit.countries.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import nl.jovmit.countries.data.remote.CountriesApi
import nl.jovmit.countries.data.repository.CountriesRepository
import nl.jovmit.countries.ui.viewmodel.CountriesViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private val json = Json { ignoreUnknownKeys = true }

val networkModule = module {
  single<OkHttpClient> {
    OkHttpClient.Builder().build()
  }

  single<Retrofit> {
    val contentType = "application/json".toMediaType()
    Retrofit.Builder()
      .baseUrl("https://jsonmock.hackerrank.com/")
      .client(get())
      .addConverterFactory(json.asConverterFactory(contentType))
      .build()
  }

  single<CountriesApi> { get<Retrofit>().create(CountriesApi::class.java) }

  single { CountriesRepository(get()) }

  viewModel {
    CountriesViewModel(get())
  }
}