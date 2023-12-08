package id.ac.umn.kevinsorensen.bengkelonline.views

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.umn.kevinsorensen.bengkelonline.SettingsApplication
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.LoginViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.main.LoginActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.main.RegisterActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.merchant.HomeMerchant
import id.ac.umn.kevinsorensen.bengkelonline.views.user.UserActivity

class MainActivity : ComponentActivity() {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsStore")
    private var permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.CAMERA
    )

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = this;
        val preferenceWrapper = (application as SettingsApplication).settingsStore;
        val loginViewModel = ViewModelProvider(this, object:
            ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(preferenceWrapper){
                    var intent: Intent =
                        if (it.role == "merchant") {
                            Intent(activity, HomeMerchant::class.java)
                        } else
                            Intent(activity, UserActivity::class.java)
                    intent.putExtra("userId", it.id);


                    if (permissions.all { permission ->
                            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                        }) {
                        activity.startActivity(intent)
                    } else {
                        // Izin belum diberikan, minta izin
                        requestPermissions()
                    }
                } as T;
            }
        })[LoginViewModel::class.java]

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                // All permissions are granted
                setContent {
                    LoginActivity(this, loginViewModel)
                }
            } else {
                finish()
            }
        }
        requestPermissions()
    }

    companion object {
        private const val TAG = "MainActivity";
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(permissions)
    }

}

