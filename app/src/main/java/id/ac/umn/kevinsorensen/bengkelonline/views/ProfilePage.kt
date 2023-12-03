package id.ac.umn.kevinsorensen.bengkelonline.views

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User

class ProfilePage: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContent{
            NormalProfilePage(null);
        }
    }
}

@Composable
fun NormalProfilePage(user: User?){
    Column {
        // image
    }
}