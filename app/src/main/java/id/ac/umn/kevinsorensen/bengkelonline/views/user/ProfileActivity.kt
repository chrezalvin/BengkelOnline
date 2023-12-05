package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.api.ResourceCollector
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User


class ProfileActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId");
        Log.d(TAG, "userId: $userId");

        if(userId == null){
            // TODO show error
            return;
        }

        val db = Firebase;
        val userController = UserController(db.firestore);
        val ResourceCollector = ResourceCollector(db.storage);


        userController.getUserById(userId){
            setContent {
                var profileImgurl by remember { mutableStateOf("") }

                    Scaffold(
                        topBar = {
                            TopNavigation2(this)
                        },
                        content = { p ->
                            val navController = rememberNavController()
                            Column(modifier = Modifier.padding(p)) {
                                if (it != null) {
                                    ResourceCollector.getProfilePhoto(it.photo) {
                                        profileImgurl = it.toString();
                                    }

                                    UserContent(user = it, profileImgurl);
                                } else
                                    errorPage()
                            }
                        }
                    )
            }
        }
    }

    companion object {
        private const val TAG = "ProfileActivity";
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation2(activity: ComponentActivity) {
    val mContext = LocalContext.current
    val contextForToast = LocalContext.current.applicationContext

    TopAppBar(
        title = {
            Text(text = "Profile")
        },
        navigationIcon = {
            IconButton(onClick = {
                // destroy activity
                activity.finish();
            }
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserContent(user: User, imageUrl: String){
    Column() {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 50.dp)
        ) {
            GlideImage(
                model = imageUrl,
                contentDescription = "photo profile",
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .clip(CircleShape)
                    .padding(20.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                ,
            ) {
                Text(text = user.name, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(text = user.email, fontSize = 10.sp)
                Text(text = user.phone, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun errorPage(){
    Column {
        Text(text = "Error")
    }
}