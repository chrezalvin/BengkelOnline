package id.ac.umn.kevinsorensen.bengkelonline.views.merchant

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.views.user.BottomNavItem
import id.ac.umn.kevinsorensen.bengkelonline.views.user.BottomNavigation
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HomePhone
import id.ac.umn.kevinsorensen.bengkelonline.views.user.TopNavigation

class HomeMerchant : ComponentActivity() {
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

