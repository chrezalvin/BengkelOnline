package id.ac.umn.kevinsorensen.bengkelonline.views.user

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeMaps(activity: ComponentActivity, coroutineScope: CoroutineScope) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    // Create a location request
    val locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(5000) // Update interval in milliseconds

    // Initialize camera position with a default location
    val defaultLocation = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Initialize the marker with a default location
        val markerState = rememberMarkerState(position = defaultLocation)

        // Request location updates
        val locationPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission is granted, request location updates
                    coroutineScope.launch {
                        try {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : com.google.android.gms.location.LocationCallback() {
                                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                        val location = locationResult.lastLocation
                                        val latLng = location?.let {
                                            LatLng(it.latitude, it.longitude)
                                        }

                                        // Update camera position and marker position with the new location
                                        latLng?.let {
                                            cameraPositionState.position =
                                                CameraPosition.fromLatLngZoom(it, 15f)
                                            markerState.position = it
                                        }
                                    }
                                },
                                null
                            )
                        } catch (e: SecurityException) {
                            // Handle permission denied
                        }
                    }
                }
            }

        // Check for location permission and request it if necessary
        val hasLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
        if (activity.shouldShowRequestPermissionRationale(hasLocationPermission)) {
            // Explain to the user why we need the location permission
        } else {
            locationPermissionLauncher.launch(hasLocationPermission)
        }

        // Display the marker with the updated position
        Marker(
            state = markerState,
            title = "My Location",
            snippet = "Current Location",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        )
    }
}
