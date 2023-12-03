package id.ac.umn.kevinsorensen.bengkelonline

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class RegisterUser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RegisterUsers()
        }
    }
}

@Composable
fun RegisterUsers() {
    Text("Hello Register User")
}
