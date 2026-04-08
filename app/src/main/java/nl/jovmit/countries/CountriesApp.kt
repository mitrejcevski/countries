package nl.jovmit.countries

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CountriesApp : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@CountriesApp)
      modules(networkModule)
    }
  }
}