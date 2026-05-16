package com.yakshaganaloka.app.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yakshaganaloka.app.models.Artist
import com.yakshaganaloka.app.models.User
import com.yakshaganaloka.app.repository.FirestoreRepository
import com.yakshaganaloka.app.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val storageRepository: StorageRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val TAG = "ProfileViewModel"

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _favoriteArtists = MutableStateFlow<List<Artist>>(emptyList())
    val favoriteArtists: StateFlow<List<Artist>> = _favoriteArtists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var profileJob: Job? = null

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "No user logged in, clearing profile state")
            clearData()
            return
        }

        Log.d(TAG, "Fetching profile for UID: ${currentUser.uid}")
        
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getUserProfileFlow(currentUser.uid).collect { profile ->
                _isLoading.value = false
                if (profile == null) {
                    Log.d(TAG, "No profile found in Firestore, creating default")
                    val newProfile = User(
                        userId = currentUser.uid,
                        name = currentUser.displayName ?: "Yakshagana Fan",
                        email = currentUser.email ?: "",
                        profileImageUrl = currentUser.photoUrl?.toString() ?: ""
                    )
                    repository.saveUserProfile(newProfile)
                    _userProfile.value = newProfile
                } else {
                    Log.d(TAG, "Profile loaded for UID: ${profile.userId}")
                    _userProfile.value = profile
                    
                    // Fetch favorite artists details
                    if (profile.favoriteArtists.isNotEmpty()) {
                        loadFavoriteArtists(profile.favoriteArtists)
                    } else {
                        _favoriteArtists.value = emptyList()
                    }
                }
            }
        }
    }

    private fun loadFavoriteArtists(artistIds: List<String>) {
        viewModelScope.launch {
            try {
                val allArtists = repository.getAllArtists()
                _favoriteArtists.value = allArtists.filter { it.id in artistIds }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading favorite artists", e)
            }
        }
    }

    fun toggleFavorite(artistId: String) {
        val currentUser = _userProfile.value ?: return
        val isFavorite = currentUser.favoriteArtists.contains(artistId)
        val updatedFavorites = if (isFavorite) {
            currentUser.favoriteArtists.filter { it != artistId }
        } else {
            currentUser.favoriteArtists + artistId
        }
        
        val updatedUser = currentUser.copy(favoriteArtists = updatedFavorites)
        viewModelScope.launch {
            try {
                repository.saveUserProfile(updatedUser)
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite", e)
            }
        }
    }

    fun updateUserProfile(name: String, profileImageUrl: String) {
        val currentUser = _userProfile.value ?: return
        val updatedUser = currentUser.copy(name = name, profileImageUrl = profileImageUrl)
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.saveUserProfile(updatedUser)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        val currentUser = _userProfile.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val downloadUrl = storageRepository.uploadProfileImage(currentUser.userId, uri)
                val updatedUser = currentUser.copy(profileImageUrl = downloadUrl)
                repository.saveUserProfile(updatedUser)
                Log.d(TAG, "Profile image uploaded successfully: $downloadUrl")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload profile image", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePreferences(notifications: Boolean, newsletter: Boolean) {
        val currentUser = _userProfile.value ?: return
        val updatedUser = currentUser.copy(
            notificationsEnabled = notifications,
            newsletterEnabled = newsletter
        )
        viewModelScope.launch {
            try {
                repository.saveUserProfile(updatedUser)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating preferences", e)
            }
        }
    }

    fun clearData() {
        Log.d(TAG, "Clearing user data state")
        profileJob?.cancel()
        _userProfile.value = null
        _favoriteArtists.value = emptyList()
    }

    fun logout() {
        auth.signOut()
        clearData()
    }
}
