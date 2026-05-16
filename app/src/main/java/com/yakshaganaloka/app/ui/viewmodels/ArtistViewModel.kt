package com.yakshaganaloka.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakshaganaloka.app.models.Artist
import com.yakshaganaloka.app.models.ArtistDummyData
import com.yakshaganaloka.app.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {
    private val TAG = "ArtistViewModel"

    // Initialize with legendary dummy data so the Encyclopedia is never empty on first load
    private val _artists = MutableStateFlow<List<Artist>>(ArtistDummyData.artists)
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchArtists()
    }

    fun fetchArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Syncing artists from Firestore...")
            
            repository.getArtistsRealtime()
                .catch { e ->
                    Log.e(TAG, "Firestore sync failed: ${e.message}")
                    // Keep dummy data on failure
                    _isLoading.value = false
                }
                .collect { list ->
                    // Filter out invalid/empty documents from Firestore
                    val validList = list.filter { it.name.isNotBlank() }
                    
                    if (validList.isNotEmpty()) {
                        Log.d(TAG, "Loaded ${validList.size} artists from Firestore")
                        _artists.value = validList
                    } else {
                        Log.d(TAG, "Firestore collection empty or invalid. Using Encyclopedia dummy data.")
                        // Restore dummy data if Firestore explicitly returns nothing
                        if (_artists.value.isEmpty() || _artists.value.any { it.name.isBlank() }) {
                            _artists.value = ArtistDummyData.artists
                        }
                    }
                    _isLoading.value = false
                }
        }
    }

    fun addArtist(artist: Artist) {
        viewModelScope.launch {
            try {
                repository.addArtist(artist)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add artist", e)
            }
        }
    }

    fun updateArtist(artist: Artist) {
        viewModelScope.launch {
            try {
                repository.updateArtist(artist)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update artist", e)
            }
        }
    }

    fun deleteArtist(artistId: String) {
        viewModelScope.launch {
            try {
                repository.deleteArtist(artistId)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete artist", e)
            }
        }
    }
}
