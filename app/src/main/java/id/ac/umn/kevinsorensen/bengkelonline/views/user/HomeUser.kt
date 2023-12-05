package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.ResourceCollector
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product

sealed class BottomNavItem (
    var icon: Int,
    var route: String
) {
    object Home :
        BottomNavItem(
            R.drawable.baseline_home_24, "home"
        )
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
                        startDestination = BottomNavItem.Home.route,
                    ) {
                        composable(BottomNavItem.Home.route) {
                            // Replace with your HomeScreen composable
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 100.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                try {
                                    productController.getAllProduct() {
                                        products = it
                                    }
                                }
                                catch (ex: Exception){
                                    Toast.makeText(this@HomeUser, ex.message, Toast.LENGTH_SHORT).show()
                                }
                                Column {
                                    products.forEach {
                                        itemProduct(it)
                                    }
                                }
                            }
                        }
                        composable(BottomNavItem.Maps.route) {
                            // Replace with your MapsScreen composable
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 80.dp),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                HomeMaps()
                                Text("Maps")
                            }
                        }
                        composable(BottomNavItem.Phone.route) {
                            // Replace with your PhoneScreen composable
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 100.dp),
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

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun itemProduct(
    product: Product,
    modifier: Modifier = Modifier
) {
    val resourceCollector = ResourceCollector(Firebase.storage);
    var image by remember { mutableStateOf("") }

    resourceCollector.getImageResource(product.imageUrl?: ""){
        image = it.toString();
    }
    
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search))
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Row{
        GlideImage(
            model = image,
            contentDescription = product.name,
            Modifier
                .width(60.dp)
                .height(40.dp)
        )
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, Color.Black)
        ) {
            Text(
                text = product.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(4.dp)
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(4.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth(1f)
                        .weight(1f)
                ) {
                    Text(text = "Price")
                    Text(text = "Stock")
                    Text(text = "Description")
                }
                Column(
                    Modifier
                        .fillMaxWidth(1f)
                        .weight(1f)
                ) {
                    Text(text = product.price.toString())
                    Text(text = product.stock.toString())
                    Text(text = product.description)
                }
            }
        }
    }
/*
    Column(
        Modifier.border(1.dp, Color.Black)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, Color.Black)
        ) {
            Text(
                text = product.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(4.dp)
                )
        }

        Row(

        ) {
            Column(
                Modifier.fillMaxWidth(1f).weight(1f)
            ) {
                Text(text = "Price")
                Text(text = "Stock")
                Text(text = "Description")
            }
            Column(
                Modifier.fillMaxWidth(1f).weight(1f)
            ) {
                Text(text = product.price.toString())
                Text(text = product.stock.toString())
                Text(text = product.description)
            }
        }
    }
*/
}

@Preview
@Composable
fun itemProductPreview(){
    itemProduct(product = Product("1", "Ban", 100000, 10, "Ban Motor"));
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopNavigation(username: String, profileUrl: String?, userId: String) {
    val contextForToast = LocalContext.current.applicationContext
    val mContext = LocalContext.current
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
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null
                  )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(username) },
                        onClick = { mContext.startActivity(
                            Intent(mContext, ProfileActivity::class.java)
                                .putExtra("userId", userId)
                        ) }
                    )
                    DropdownMenuItem(
                        text = { Text("Log Out") },
                        onClick = { mContext.startActivity(Intent(mContext, MainActivity::class.java)) }
                    )
                }
        },
    )
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf (
        BottomNavItem.Maps,
        BottomNavItem.Home,
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