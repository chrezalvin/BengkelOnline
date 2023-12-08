package id.ac.umn.kevinsorensen.bengkelonline.views.merchant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.HomeViewModel
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.MerchantViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.user.BottomNavItem
import id.ac.umn.kevinsorensen.bengkelonline.views.user.BottomNavigation
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HistoryActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HomePhone
import id.ac.umn.kevinsorensen.bengkelonline.views.user.ProfileActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.user.TopNavigation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    val orderDate: Date,
    val latitude: Double,
    val longitude: Double,
    val shopName: String,
    val status: String
)

class OrderDataSource {
    fun loadOrders(): List<Order> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return listOf(
            Order(dateFormat.parse("01-03-2023")!!, -6.175110, 106.865036, "Shop X", "Completed"),
            Order(dateFormat.parse("05-03-2023")!!, -6.208763, 106.845599, "Shop Y", "In Progress"),
            Order(dateFormat.parse("10-03-2023")!!, -6.914744, 107.609810, "Shop Z", "Cancelled"),
            Order(dateFormat.parse("15-03-2023")!!, -7.795580, 110.369490, "Shop W", "Cancelled"),
            Order(dateFormat.parse("20-03-2023")!!, -6.932444, 107.604738, "Shop V", "In Progress"),
            Order(dateFormat.parse("25-03-2023")!!, -3.917464, 107.619123, "Shop U", "In Progress"),
            Order(dateFormat.parse("01-04-2023")!!, -7.257472, 112.752090, "Shop T", "Completed"),
            Order(dateFormat.parse("05-04-2023")!!, -10.966620, 112.632629, "Shop S", "Completed"),
            Order(dateFormat.parse("10-04-2023")!!, -6.175392, 106.827153, "Shop R", "Cancelled"),
            Order(dateFormat.parse("15-04-2023")!!, -8.402484, 106.794243, "Shop Q", "Cancelled")
        )
    }
}

class HomeMerchant : ComponentActivity() {
    private var permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.INTERNET
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(100)
                .setMaxUpdateDelayMillis(100)
                .build()

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get userId
        val userId = intent.getStringExtra("userId")?: "0";
        val userName = intent.getStringExtra("username")?: "user";
        val profileUrl = intent.getStringExtra("profileUrl");

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val merchantViewModel: MerchantViewModel = viewModel();
            val uiState by merchantViewModel.uiState.collectAsState();

            val cameraPosition = rememberCameraPositionState{
                position = CameraPosition.fromLatLngZoom(
                    merchantViewModel.userLocation, 10f
                )
            }

            var cameraPositionState by remember {
                mutableStateOf(cameraPosition)
            }

            // Di dalam UserActivity atau di mana `cameraPositionState` didefinisikan
            var isZoomLevelUserSet = false

            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    p0.locations.lastOrNull()?.let { location ->
                        merchantViewModel.updateUserLocation(location.latitude, location.longitude)

                        val zoomLevelToUse = if (isZoomLevelUserSet) {
                            cameraPositionState.position.zoom
                        } else {
                            // Setelah zoom level pertama kali diatur, anggap pengguna telah mengatur zoom
                            isZoomLevelUserSet = true
                            16f // Default zoom level
                        }

                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            merchantViewModel.userLocation, zoomLevelToUse
                        )
                    }
                }
            }

            var products by remember { mutableStateOf(listOf<Product>()) }

            val navController = rememberNavController()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopNavigation2(userName, profileUrl, userId, navController = navController)
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
                                LocationScreen(
                                    merchantViewModel.userLocation,
                                    cameraPositionState,
                                    uiState.complaints
                                )
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
                                displayOrders(
                                    uiState.complaints,
                                    onUpdate = {
                                        merchantViewModel.getNearbyComplaints()
                                    },
                                    navigateToDetail = { index ->
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun LocationScreen(
        currentLocation: LatLng,
        camerapositionState: CameraPositionState,
        complaints: List<Complaint> = listOf()
    ) {
        val context = LocalContext.current

        val launchMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
                permissionMaps ->
            val areGranted = permissionMaps.values.reduce {acc, next -> acc && next}
            if (areGranted) {
                locationRequired = true
                startLocationUpdates()
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap (
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camerapositionState
            ) {
                Marker(
                    state = MarkerState(
                        position = currentLocation,
                    ),
                    title = "You",
                    snippet = "You're here!!!"
                )

                for(complaint in complaints){
                    Marker(
                        state = MarkerState(
                            position = LatLng(complaint.lat.toDouble(), complaint.long.toDouble()),
                        ),
                        title = complaint.id,
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Your Location: ${currentLocation.latitude}/${currentLocation.longitude}")
                if (permissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    })
                {
                    // Get Location
                    (context as? HomeMerchant)?.startLocationUpdates()
                }
                else {
                    launchMultiplePermissions.launch(permissions)
                }
            }
        }
    }
}

fun MerchantPhone() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun complaintCard(
    complaint: Complaint,
    index: Int,
    onClick: () -> Unit
){
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = "Order Date: ${formatDate(complaint.date.toDate())}",
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            color = Color.Black
        )
        Text(
            text = "Longitude: ${complaint.long}",
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Latitude: ${complaint.lat}",
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Shop Name: ${complaint.userId}",
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun displayOrders(
    complaints: List<Complaint>,
    navigateToDetail: (Int) -> Unit,
    onUpdate: () -> Unit
) {
    LazyColumn {
        itemsIndexed(complaints) { index, complaint ->
            complaintCard(complaint, index, onClick = { navigateToDetail(index) })
            Spacer(modifier = Modifier.height(10.dp))
        }
        this.item {
            Button(onClick = { onUpdate() }) {
                Text(text = "Update")
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopNavigation2(username: String, profileUrl: String?, userId: String, navController: NavController) {
    val contextForToast = LocalContext.current.applicationContext
    val mContext = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var MenuName by remember { mutableStateOf("Lokasi Anda") } // Set judul awal

    DisposableEffect(navController) {
        val callback = NavController.OnDestinationChangedListener { controller, _, _ ->
            // Mengubah judul berdasarkan rute yang saat ini dipilih
            MenuName = when (controller.currentDestination?.route) {
                BottomNavItem.Maps.route -> "Lokasi Anda"
                BottomNavItem.Phone.route -> "New Order"
                else -> "Judul Default" // Gantilah dengan judul default Anda
            }
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = MenuName,
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

