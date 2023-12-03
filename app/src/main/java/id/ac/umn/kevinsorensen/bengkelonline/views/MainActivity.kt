package id.ac.umn.kevinsorensen.bengkelonline.views

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import id.ac.umn.kevinsorensen.bengkelonline.views.main.LoginActivity

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginActivity()
        }
    }

    companion object {
        private const val TAG = "MainActivity";
    }
}

