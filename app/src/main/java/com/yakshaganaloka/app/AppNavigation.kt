package com.yakshaganaloka.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.yakshaganaloka.app.ui.screens.ArtistDetailScreen
import com.yakshaganaloka.app.ui.screens.ArtistDirectoryScreen
import com.yakshaganaloka.app.ui.screens.HomeScreen
import com.yakshaganaloka.app.ui.screens.LoginScreen
import com.yakshaganaloka.app.ui.screens.ManagerPortalScreen
import com.yakshaganaloka.app.ui.screens.MapScreen
import com.yakshaganaloka.app.ui.screens.ProfileScreen
import com.yakshaganaloka.app.ui.screens.RadioScreen
import com.yakshaganaloka.app.ui.screens.SettingsScreen
import com.yakshaganaloka.app.ui.screens.SignUpScreen
import com.yakshaganaloka.app.ui.viewmodels.AuthState
import com.yakshaganaloka.app.ui.viewmodels.AuthViewModel
import com.yakshaganaloka.app.ui.viewmodels.ProfileViewModel
import com.yakshaganaloka.app.ui.viewmodels.ThemeViewModel
import com.yakshaganaloka.app.ui.viewmodels.AudioViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Map : Screen("map_full", "Performance Map", Icons.Filled.Map)
    data object Artists : Screen("artists", "Artists", Icons.Filled.Person)
    data object ArtistDetail : Screen("artist_detail/{artistId}", "Artist Detail", Icons.Filled.Person)
    data object Radio : Screen("radio?artistId={artistId}", "Radio", Icons.Filled.PlayArrow)
    data object Manager : Screen("manager", "Manager", Icons.Filled.Settings)
    data object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object Login : Screen("login", "Login", Icons.Filled.Person)
    data object SignUp : Screen("signup", "SignUp", Icons.Filled.Person)
}

val bottomNavItems = listOf(Screen.Home, Screen.Artists, Screen.Radio, Screen.Manager)

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    // Use separate navigation stacks for Authenticated and Guest users to prevent 
    // state restoration crashes when swapping graphs.
    if (authState is AuthState.Authenticated) {
        AuthenticatedStack(authViewModel, themeViewModel, profileViewModel)
    } else {
        GuestStack(authViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedStack(
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    profileViewModel: ProfileViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                            )
                        )
                        .clickable {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Profile.route)
                        }
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    val currentUser = firebaseAuth.currentUser
                    val displayName = userProfile?.name?.ifBlank { currentUser?.displayName } ?: currentUser?.displayName ?: "Yakshagana Fan"
                    val displayEmail = userProfile?.email?.ifBlank { currentUser?.email } ?: currentUser?.email ?: ""
                    val profilePic = userProfile?.profileImageUrl?.ifBlank { currentUser?.photoUrl?.toString() } ?: currentUser?.photoUrl?.toString()

                    Column {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profilePic.isNullOrEmpty()) {
                                AsyncImage(model = profilePic, contentDescription = "Profile", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = displayEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Performance Map") },
                    selected = false,
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Map.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Artist Directory") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Artists.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp))

                NavigationDrawerItem(
                    label = { Text("Dark Mode") },
                    selected = false,
                    icon = { Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null) },
                    badge = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { themeViewModel.toggleDarkMode(it) }
                        )
                    },
                    onClick = { themeViewModel.toggleDarkMode(!isDarkMode) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        scope.launch { 
                            drawerState.close() 
                            authViewModel.logout()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route.split("?")[0] } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController, 
                startDestination = Screen.Home.route, 
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { 
                    HomeScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateToMap = { navController.navigate(Screen.Map.route) },
                        onNavigateToArtists = { navController.navigate(Screen.Artists.route) },
                        onNavigateToRadio = { navController.navigate(Screen.Radio.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    ) 
                }
                composable(Screen.Map.route) {
                    MapScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable(Screen.Artists.route) { 
                    ArtistDirectoryScreen(
                        onArtistClick = { artist ->
                            navController.navigate("artist_detail/${artist.id}")
                        }
                    ) 
                }
                composable(
                    route = Screen.ArtistDetail.route,
                    arguments = listOf(navArgument("artistId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
                    ArtistDetailScreen(
                        artistId = artistId,
                        onNavigateBack = { navController.popBackStack() },
                        onViewPerformances = {
                            navController.navigate("radio?artistId=${artistId}") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(
                    route = Screen.Radio.route,
                    arguments = listOf(navArgument("artistId") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
                ) { 
                    val audioViewModel: AudioViewModel = hiltViewModel()
                    RadioScreen(
                        onPlayAudio = { audio -> audioViewModel.playAudio(audio) }
                    ) 
                }
                composable(Screen.Manager.route) { ManagerPortalScreen() }
                composable(Screen.Profile.route) { 
                    ProfileScreen(
                        onNavigateBack = { navController.popBackStack() }, 
                        viewModel = profileViewModel,
                        onArtistClick = { artist ->
                            navController.navigate("artist_detail/${artist.id}")
                        }
                    ) 
                }
                composable(Screen.Settings.route) { SettingsScreen(onNavigateBack = { navController.popBackStack() }, themeViewModel = themeViewModel) }
            }
        }
    }
}

@Composable
fun GuestStack(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                viewModel = authViewModel
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                viewModel = authViewModel
            )
        }
    }
}
