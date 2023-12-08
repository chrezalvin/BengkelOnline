package id.ac.umn.kevinsorensen.bengkelonline

import android.app.Application

class SettingsApplication: Application() {
    lateinit var settingsStore: SettingsStore;

    override fun onCreate() {
        super.onCreate()
        settingsStore = SettingsStore(this)
    }
}