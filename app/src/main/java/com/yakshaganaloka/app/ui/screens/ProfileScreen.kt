package com.yakshaganaloka.app.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yakshaganaloka.app.models.Artist
import com.yakshaganaloka.app.models.YakshaganaEvent
import com.yakshaganaloka.app.ui.components.YakshaganaLoading
import com.yakshaganaloka.app.ui.components.EditEventDialog
import com.yakshaganaloka.app.ui.viewmodels.EventViewModel
import com.yakshaganaloka.app.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onArtistClick: (Artist) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val favoriteArtists by viewModel.favoriteArtists.collectAsState()
    val upcomingEvents by eventViewModel.upcomingEvents.collectAsState()
    val liveEvents by eventViewModel.liveEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    
    val authUser = remember { FirebaseAuth.getInstance().currentUser }

    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    
    // Conductor Event Management State
    var eventToEdit by remember { mutableStateOf<YakshaganaEvent?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(it)
            Toast.makeText(context, "Uploading profile photo...", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            profileImageUrl = it.profileImageUrl
        }
    }

    if (eventToEdit != null) {
        EditEventDialog(
            event = eventToEdit!!,
            onDismiss = { eventToEdit = null },
            onSave = { updatedEvent ->
                eventViewModel.updateEvent(updatedEvent)
                eventToEdit = null
                Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = isEditing, enter = scaleIn(), exit = scaleOut()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.updateUserProfile(name, profileImageUrl)
                        isEditing = false
                        Toast.makeText(context, "Profile Saved", Toast.LENGTH_SHORT).show()
                    },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Save Profile") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->
        if (isLoading && userProfile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                YakshaganaLoading()
            }
        } else {
            val displayName = userProfile?.name?.ifBlank { authUser?.displayName } ?: authUser?.displayName ?: "Yakshagana Fan"
            val displayEmail = userProfile?.email?.ifBlank { authUser?.email } ?: authUser?.email ?: "No email linked"
            val displayImage = profileImageUrl.ifBlank { authUser?.photoUrl?.toString() ?: "" }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable(enabled = isEditing) { 
                                    photoPickerLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (displayImage.isNotEmpty()) {
                                AsyncImage(
                                    model = displayImage,
                                    contentDescription = "Profile",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.Gray)
                            }
                            if (isEditing) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // Stats Dashboard
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatItem("Favorites", favoriteArtists.size.toString()) {
                        Toast.makeText(context, "You follow ${favoriteArtists.size} artists", Toast.LENGTH_SHORT).show()
                    }
                    ProfileStatItem("Art Points", "1,240") {
                        Toast.makeText(context, "Earn points by attending live Melas!", Toast.LENGTH_SHORT).show()
                    }
                    ProfileStatItem("Tier", userProfile?.role?.replaceFirstChar { it.uppercase() } ?: "User") {
                        Toast.makeText(context, "Account Tier: ${userProfile?.role}", Toast.LENGTH_SHORT).show()
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Public Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = profileImageUrl,
                            onValueChange = { profileImageUrl = it },
                            label = { Text("Profile Picture URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        Text(
                            "Tip: Click the photo above to upload from gallery",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    } else {
                        Text(text = displayName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Text(text = displayEmail, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Conductor Dashboard Section
                    if (userProfile?.role == "conductor") {
                        val myEvents = (liveEvents + upcomingEvents).filter { it.conductorId == userProfile?.userId }
                        
                        Text("My Organized Performances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (myEvents.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    "No events organized yet. Head to the Manager Portal to add one.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            myEvents.forEach { event ->
                                ConductorEventItem(
                                    event = event,
                                    onEdit = { eventToEdit = it },
                                    onDelete = { 
                                        eventViewModel.deleteEvent(event.id)
                                        Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // My Favorites
                    if (favoriteArtists.isNotEmpty()) {
                        Text("My Favorite Artists", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(favoriteArtists) { artist ->
                                FavArtistItem(
                                    artist = artist,
                                    onClick = { onArtistClick(artist) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Membership Progress
                    Text("Membership Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(12.dp))
                                Text("Aabhushana Gold", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.weight(1f))
                                Text("78%", style = MaterialTheme.typography.labelMedium)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { 0.78f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("260 points to Diamond Tier", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // App Preferences
                    Text("Account Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    PreferenceItem(
                        Icons.Default.Notifications, 
                        "Live Alerts", 
                        "Notify me when Melas start", 
                        userProfile?.notificationsEnabled ?: true
                    ) { 
                        viewModel.updatePreferences(it, userProfile?.newsletterEnabled ?: false)
                    }
                    
                    PreferenceItem(
                        Icons.Default.Email, 
                        "Newsletter", 
                        "Weekly art insights", 
                        userProfile?.newsletterEnabled ?: false
                    ) { 
                        viewModel.updatePreferences(userProfile?.notificationsEnabled ?: true, it)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign Out Action
                    Button(
                        onClick = { 
                            viewModel.logout()
                            onNavigateBack() 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ConductorEventItem(
    event: YakshaganaEvent,
    onEdit: (YakshaganaEvent) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("${event.date} at ${event.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text(event.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                IconButton(onClick = { onEdit(event) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun FavArtistItem(
    artist: Artist,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (artist.imageUrl != null) {
                AsyncImage(model = artist.imageUrl, contentDescription = null, contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProfileStatItem(label: String, value: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onClick() }.padding(8.dp)
    ) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
    }
}

@Composable
fun PreferenceItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
