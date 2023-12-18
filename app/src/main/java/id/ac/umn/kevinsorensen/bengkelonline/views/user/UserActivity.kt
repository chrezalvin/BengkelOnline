package id.ac.umn.kevinsorensen.bengkelonline.views.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import id.ac.umn.kevinsorensen.bengkelonline.SettingsApplication
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.HomeViewModel
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.LoginViewModel
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

class UserActivity : ComponentActivity() {
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
        val userId = intent.getStringExtra("userId")?: "";

        if(userId == ""){
            // back to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        };

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){

        }

        val activity = this;
        val preferenceWrapper = (application as SettingsApplication).settingsStore;
        val homeViewModel = ViewModelProvider(this, object:
            ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(preferenceWrapper) as T;
            }
        })[HomeViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val uiState by homeViewModel.uiState.collectAsState();
            homeViewModel.initializeHome(userId);

/*            var currentLocation by remember {
                mutableStateOf(LatLng(0.toDouble(), 0.toDouble()))
            }*/

            val cameraPosition = rememberCameraPositionState{
                position = CameraPosition.fromLatLngZoom(
                    homeViewModel.currentLocation, 10f
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
                        homeViewModel.updateCurrentLocation(location.latitude.toFloat(), location.longitude.toFloat())

                        val zoomLevelToUse = if (isZoomLevelUserSet) {
                            cameraPositionState.position.zoom
                        } else {
                            // Setelah zoom level pertama kali diatur, anggap pengguna telah mengatur zoom
                            isZoomLevelUserSet = true
                            16f // Default zoom level
                        }

                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            homeViewModel.currentLocation, zoomLevelToUse
                        )
                    }
                }
            }

            val navController = rememberNavController()

            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    if(uiState.user != null)
                        TopNavigation(
                            uiState.user!!.username,
                            uiState.profilePhotoUri?.toString() ?: "",
                            userId,
                            navController = navController,
                            onLogout = {
                                homeViewModel.logout {
                                    val intent = Intent(activity, MainActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()
                                }
                            }
                        )
                    else
                        TopNavigation("User", null, userId, navController = navController)
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
                                LocationScreen(homeViewModel.currentLocation, cameraPositionState)
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
                                HomePhone(
                                    homeViewModel.currentLocation,
                                    problemDesc = homeViewModel.complaintDescription,
                                    bitmaps = uiState.bitmaps,

                                    onProblemDescChange = {
                                        homeViewModel.updateComplaintDescription(it)
                                    },
                                    onOrder = {
                                        homeViewModel.orderComplaint {
                                            homeViewModel.resetInputs()
                                        }
                                    },
                                    onBitmapUpdate = { index, bitmap ->
                                        homeViewModel.updateBitmaps(index, bitmap)
                                    },

                                    error = uiState.error,
                                    bitmapError = uiState.bitmapError,
                                    locationError = uiState.locationError,
                                    complaintError = uiState.complaintError,
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
        camerapositionState: CameraPositionState
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
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (permissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    })
                {
                    // Get Location
                    (context as? UserActivity)?.startLocationUpdates()
                    Text(text = "Your Location: ${currentLocation.latitude}/${currentLocation.longitude}")
                }
                else {
                    launchMultiplePermissions.launch(permissions)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopNavigation(
    username: String,
    profileUrl: String?,
    userId: String,
    navController: NavController,
    onLogout: () -> Unit = { }
) {
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
                BottomNavItem.Phone.route -> "Panggil Bengkel"
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
                                    onClick = { onLogout() }
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
    var selectedRoute by remember { mutableStateOf(BottomNavItem.Maps.route) }
    val items = listOf(
        BottomNavItem.Maps,
        BottomNavItem.Phone
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            AddItem(
                screen = item,
                navController = navController,
                isSelected = item.route == selectedRoute
            ) {
                selectedRoute = item.route
            }
        }
    }
}

@Composable
fun AddItem(
    screen: BottomNavItem,
    navController: NavController,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route)
                onSelected()
            }
            .background(Color.Red)
            .padding(25.dp)
    ) {
        Icon(
            painterResource(id = screen.icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}