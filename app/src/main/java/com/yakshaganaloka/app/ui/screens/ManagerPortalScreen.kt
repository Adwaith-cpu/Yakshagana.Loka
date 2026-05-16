package com.yakshaganaloka.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.yakshaganaloka.app.models.Artist
import com.yakshaganaloka.app.models.Audio
import com.yakshaganaloka.app.models.YakshaganaEvent
import com.yakshaganaloka.app.ui.components.EditArtistDialog
import com.yakshaganaloka.app.ui.components.EditEventDialog
import com.yakshaganaloka.app.ui.viewmodels.ArtistViewModel
import com.yakshaganaloka.app.ui.viewmodels.AudioViewModel
import com.yakshaganaloka.app.ui.viewmodels.EventViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerPortalScreen(
    artistViewModel: ArtistViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    audioViewModel: AudioViewModel = hiltViewModel()
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Artists", "Events", "Talamaddale Radio")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
            }
        }

        when (tabIndex) {
            0 -> ManageArtistsSection(artistViewModel)
            1 -> ManageEventsSection(eventViewModel)
            2 -> ManageAudioSection(audioViewModel)
        }
    }
}

@Composable
fun ManageArtistsSection(viewModel: ArtistViewModel) {
    var showAddForm by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Artist Management", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showAddForm = !showAddForm }) {
                Icon(if (showAddForm) Icons.AutoMirrored.Filled.List else Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (showAddForm) "View All" else "Add New")
            }
        }
        
        if (showAddForm) {
            AddArtistForm(viewModel)
        } else {
            ManageArtistsList(viewModel)
        }
    }
}

@Composable
fun ManageArtistsList(viewModel: ArtistViewModel) {
    val artists by viewModel.artists.collectAsState()
    var artistToEdit by remember { mutableStateOf<Artist?>(null) }
    var artistToDelete by remember { mutableStateOf<Artist?>(null) }

    if (artistToEdit != null) {
        EditArtistDialog(
            artist = artistToEdit!!,
            onDismiss = { artistToEdit = null },
            onSave = { updatedArtist ->
                viewModel.updateArtist(updatedArtist)
                artistToEdit = null
            }
        )
    }

    if (artistToDelete != null) {
        AlertDialog(
            onDismissRequest = { artistToDelete = null },
            title = { Text("Delete Artist") },
            text = { Text("Are you sure you want to delete '${artistToDelete!!.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteArtist(artistToDelete!!.id)
                        artistToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { artistToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(artists) { artist ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(artist.name, fontWeight = FontWeight.Bold)
                        Text(artist.role, style = MaterialTheme.typography.bodySmall)
                    }
                    Row {
                        IconButton(onClick = { artistToEdit = artist }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { artistToDelete = artist }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManageEventsSection(viewModel: EventViewModel) {
    var showAddForm by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Events Management", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showAddForm = !showAddForm }) {
                Icon(if (showAddForm) Icons.AutoMirrored.Filled.List else Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (showAddForm) "View All" else "Add New")
            }
        }
        
        if (showAddForm) {
            AddEventForm(viewModel)
        } else {
            ManageEventsList(viewModel)
        }
    }
}

@Composable
fun ManageAudioSection(viewModel: AudioViewModel) {
    var showAddForm by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Radio Content", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showAddForm = !showAddForm }) {
                Icon(if (showAddForm) Icons.AutoMirrored.Filled.List else Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (showAddForm) "View All" else "Add New")
            }
        }
        
        if (showAddForm) {
            AddAudioForm(viewModel)
        } else {
            ManageAudioList(viewModel)
        }
    }
}

@Composable
fun AddAudioForm(viewModel: AudioViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Talamaddale") }
    var youtubeUrl by remember { mutableStateOf("") }
    var isFeatured by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add New Talamaddale Program", style = MaterialTheme.typography.titleLarge)
        
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Program Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = artistName, onValueChange = { artistName = it }, label = { Text("Main Artist") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = youtubeUrl, onValueChange = { youtubeUrl = it }, label = { Text("YouTube URL") }, modifier = Modifier.fillMaxWidth())
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
            Text("Feature this program")
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                val videoId = extractVideoId(youtubeUrl)
                if (videoId != null) {
                    val audio = Audio(
                        title = title,
                        artistName = artistName,
                        description = description,
                        category = category,
                        youtubeUrl = youtubeUrl,
                        videoId = videoId,
                        thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg",
                        isFeatured = isFeatured
                    )
                    viewModel.addAudio(audio)
                    title = ""; artistName = ""; description = ""; youtubeUrl = ""
                    Toast.makeText(context, "Audio Added Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Invalid YouTube URL", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty() && youtubeUrl.isNotEmpty()
        ) {
            Text("Save to Radio")
        }
    }
}

@Composable
fun ManageAudioList(viewModel: AudioViewModel) {
    val audioList by viewModel.audioList.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(audioList) { audio ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(audio.title, fontWeight = FontWeight.Bold)
                        Text(audio.artistName, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { viewModel.deleteAudio(audio.audioId) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

fun extractVideoId(url: String): String? {
    return try {
        if (url.contains("v=")) {
            url.split("v=")[1].split("&")[0]
        } else if (url.contains("youtu.be/")) {
            url.split("youtu.be/")[1].split("?")[0]
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AddArtistForm(viewModel: ArtistViewModel) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var vesha by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add New Artist", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (Bhagavatha/Actor/etc)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = vesha, onValueChange = { vesha = it }, label = { Text("Iconic Vesha") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        
        Button(
            onClick = {
                viewModel.addArtist(Artist(name = name, role = role, vesha = vesha, description = description, imageUrl = imageUrl.ifBlank { null }))
                name = ""; role = ""; vesha = ""; description = ""; imageUrl = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Artist")
        }
    }
}

@Composable
fun ManageEventsList(viewModel: EventViewModel) {
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    val liveEvents by viewModel.liveEvents.collectAsState()
    val allEvents = liveEvents + upcomingEvents
    
    var eventToEdit by remember { mutableStateOf<YakshaganaEvent?>(null) }
    var eventToDelete by remember { mutableStateOf<YakshaganaEvent?>(null) }

    if (eventToEdit != null) {
        EditEventDialog(
            event = eventToEdit!!,
            onDismiss = { eventToEdit = null },
            onSave = { updatedEvent ->
                viewModel.updateEvent(updatedEvent)
                eventToEdit = null
            }
        )
    }

    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Delete Event") },
            text = { Text("Are you sure you want to delete '${eventToDelete!!.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(eventToDelete!!.id)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Upcoming & Live Events", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        
        if (allEvents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No events found to manage.")
                }
            }
        }

        items(allEvents) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(event.melaName, style = MaterialTheme.typography.bodySmall)
                        Text("${event.date} at ${event.time}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(event.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Row {
                        IconButton(onClick = { eventToEdit = event }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { eventToDelete = event }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEventForm(viewModel: EventViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var melaName by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    val udupi = LatLng(13.3409, 74.7421)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(udupi, 10f)
    }

    val moveToUserLocation = {
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {}
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add New Event", style = MaterialTheme.typography.titleLarge)
        
        Text("Pick Location on Map", style = MaterialTheme.typography.titleSmall)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false),
                onMapClick = { point ->
                    lat = String.format(Locale.US, "%.6f", point.latitude)
                    lng = String.format(Locale.US, "%.6f", point.longitude)
                }
            ) {
                if (lat.isNotEmpty() && lng.isNotEmpty()) {
                    Marker(
                        state = rememberMarkerState(position = LatLng(lat.toDouble(), lng.toDouble())),
                        title = "Performance Spot"
                    )
                }
            }
            
            // Accuracy booster: Center on current location button
            IconButton(
                onClick = { moveToUserLocation() },
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = melaName, onValueChange = { melaName = it }, label = { Text("Mela Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = locationName, onValueChange = { locationName = it }, label = { Text("Location Name (e.g. Udupi)") }, modifier = Modifier.fillMaxWidth())
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (9:30 PM)") }, modifier = Modifier.weight(1f))
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Longitude") }, modifier = Modifier.weight(1f))
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Event Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                val event = YakshaganaEvent(
                    title = title,
                    melaName = melaName,
                    location = locationName,
                    date = date,
                    time = time,
                    latitude = lat.toDoubleOrNull() ?: 0.0,
                    longitude = lng.toDoubleOrNull() ?: 0.0,
                    description = description
                )
                viewModel.addEvent(event)
                title = ""; melaName = ""; locationName = ""; date = ""; time = ""; lat = ""; lng = ""; description = ""
                Toast.makeText(context, "Event Saved Successfully", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = lat.isNotEmpty() && lng.isNotEmpty()
        ) {
            Text("Save Event")
        }
    }
}
