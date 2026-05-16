package com.yakshaganaloka.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.yakshaganaloka.app.ui.viewmodels.MapViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerformanceMap(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val fusedLocationClient: FusedLocationProviderClient = remember { 
        LocationServices.getFusedLocationProviderClient(context) 
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    var hasInitializedCamera by remember { mutableStateOf(false) }

    val udupi = LatLng(13.3409, 74.7421)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(udupi, 8f)
    }

    // Function to move camera to user location
    val moveToUserLocation = {
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                12f
                            )
                        )
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception
            }
        }
    }

    // Initial camera placement
    LaunchedEffect(events, hasLocationPermission) {
        if (!hasInitializedCamera) {
            if (events.isNotEmpty()) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(events[0].latitude, events[0].longitude),
                        10f
                    )
                )
                hasInitializedCamera = true
            } else if (hasLocationPermission) {
                moveToUserLocation()
                hasInitializedCamera = true
            }
        }
    }

    // Permission request
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val properties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(isMyLocationEnabled = hasLocationPermission))
    }
    
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(myLocationButtonEnabled = false)) // Custom button used instead
    }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            if (event.latitude != 0.0 && event.longitude != 0.0) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(event.latitude, event.longitude),
                        15f
                    ),
                    durationMs = 1000
                )
            }
        }
    }

    Box(modifier = modifier) {
        if (isLoading && events.isEmpty()) {
            YakshaganaLoading()
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            ) {
                events.forEach { event ->
                    if (event.latitude != 0.0 && event.longitude != 0.0) {
                        val isLive = event.date == today
                        // Using a compound key to ensure the marker state resets if location changes
                        val markerKey = "${event.id}_${event.latitude}_${event.longitude}"
                        val markerState = rememberMarkerState(key = markerKey, position = LatLng(event.latitude, event.longitude))
                        
                        Marker(
                            state = markerState,
                            title = if (isLive) "LIVE: ${event.melaName}" else event.melaName,
                            snippet = "${event.title} - ${event.date}",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (isLive) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_AZURE
                            ),
                            onClick = {
                                viewModel.selectEvent(event)
                                false
                            }
                        )
                    }
                }
            }

            // Custom My Location Button for better control
            if (hasLocationPermission) {
                FloatingActionButton(
                    onClick = { moveToUserLocation() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(40.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
