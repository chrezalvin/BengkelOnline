package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity

sealed class BottomNavItem (
    var icon: Int,
    var route: String
) {
    object Maps :
            BottomNavItem(
                R.drawable.baseline_location_on_24, "maps"
            )
    object Phone :
            BottomNavItem(
                R.drawable.baseline_phone_24, "phone"
            )
}

class HomeUser : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get userId
        val userId = intent.getStringExtra("userId")?: "0";
        val userName = intent.getStringExtra("username")?: "user";
        val profileUrl = intent.getStringExtra("profileUrl");

        setContent {


            var products by remember { mutableStateOf(listOf<Product>()) }

            val navController = rememberNavController()
            val db = Firebase;
            val productController = ProductController(db.firestore);
            val userController = UserController(db);

            userController.getUser("admin", "admin"){
                Log.d("HomeUser", it.toString())
            }
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopNavigation(userName, profileUrl, userId)
                },
                bottomBar = {
                    BottomNavigation(navController = navController)
                },
                content = {
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Maps.route,
                    ) {
                        composable(BottomNavItem.Maps.route) {
                            // Replace with your MapsScreen composable
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 80.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                // Implement Maps 
                            }
                        }
                        composable(BottomNavItem.Phone.route) {
                            // Replace with your PhoneScreen composable
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 80.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                HomePhone()
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopNavigation(username: String, profileUrl: String?, userId: String) {
    val contextForToast = LocalContext.current.applicationContext
    val mContext = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                "Bengkel Online",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
                IconButton(onClick = { expanded = !expanded }) {
                    /*
                    GlideImage(model = profileUrl, contentDescription = username,
                        Modifier
                            .height(24.dp)
                            .width(24.dp))
                     */
                  Icon (
                        painter = painterResource(id = R.drawable.baseline_menu_24),
                        contentDescription = null
                  )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = null,
                            )
                        },
                        text = { Text(username) },
                        onClick = { mContext.startActivity(
                            Intent(mContext, ProfileActivity::class.java)
                                .putExtra("userId", userId)
                        ) },
                    )
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_history_24),
                                contentDescription = null,
                            )
                        },
                        text = { Text("History") },
                        onClick = { mContext.startActivity(Intent(mContext, HistoryActivity::class.java)) }
                    )
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_logout_24),
                                contentDescription = null,
                            )
                        },
                        text = { Text("Log Out") },
                        onClick = {
                            showLogoutDialog = true
                            // Context.startActivity(Intent(mContext, MainActivity::class.java))
                        }
                    )
                    if(showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showLogoutDialog = false
                            },
                            title = {
                                Text("Log Out")
                            },
                            text = {
                                Text("Are you sure you want to log out?")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = { mContext.startActivity(Intent(mContext, MainActivity::class.java)) }
                                ) {
                                    Text("Yes")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showLogoutDialog = false
                                    }
                                ) {
                                    Text("No")
                                }
                            }
                        )
                    }
                }
        },
    )
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf (
        BottomNavItem.Maps,
        BottomNavItem.Phone
    )
    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    navController: NavController
) {
    NavigationBarItem(
        icon = {
               Icon (
                   painterResource(id = screen.icon),
                   contentDescription = null
               )
        },
        selected = true ,
        onClick = {
            navController.navigate(screen.route)
        },
        colors = NavigationBarItemDefaults.colors()
    )
}