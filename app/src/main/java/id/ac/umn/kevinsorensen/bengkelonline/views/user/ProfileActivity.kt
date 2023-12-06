package id.ac.umn.kevinsorensen.bengkelonline.views.user

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.ProfileViewModel


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId");
        Log.d(TAG, "userId: $userId");

        setContent {
            if(userId == null)
                errorPage("User not found"){
                    finish();
                }
            else
                profilePage(this, ProfileViewModel(userId));
        }
    }

    companion object {
        private const val TAG = "ProfileActivity";
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profilePage(
    activity: ComponentActivity,
    profileViewModel: ProfileViewModel = viewModel()
){
    val profileState by profileViewModel.uiState.collectAsState();

    Scaffold(
        topBar = {
            TopNavigation2(activity)
        },
        content = { p ->
            val navController = rememberNavController()
            Column(modifier = Modifier.padding(p)) {
                if (profileState.user != null) {
                    UserContent(
                        user = profileState.user!!,
                        imageUrl = profileState.profilePhoto.toString()
                    );
                } else
                    errorPage(profileState.error){
                        activity.finish();
                    }
            }
        }
    )
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
                Text(text = user.username, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(text = user.email, fontSize = 10.sp)
                Text(text = user.phoneNumber ?: "", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun errorPage(message: String? = null, onButtonGoBack: () -> Unit){
    Column {
        Text(text = "Error")
        if(message != null)
            Text(text = message)
        Button(
            content = { Text(text = "Go Back") },
            onClick = onButtonGoBack
        )
    }
}

@Preview(showBackground = true)
@Composable
fun errorPagePreview(){
    errorPage("Error message", {});
}