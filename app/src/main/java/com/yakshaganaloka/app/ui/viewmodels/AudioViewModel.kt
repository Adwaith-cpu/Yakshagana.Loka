package com.yakshaganaloka.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakshaganaloka.app.data.audio.AudioPlayerManager
import com.yakshaganaloka.app.models.Audio
import com.yakshaganaloka.app.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val audioPlayerManager: AudioPlayerManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String? = savedStateHandle["artistId"]

    private val _audioList = MutableStateFlow<List<Audio>>(emptyList())
    val audioList: StateFlow<List<Audio>> = _audioList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val isPlaying = audioPlayerManager.isPlaying
    val currentAudio = audioPlayerManager.currentAudio
    val playbackState = audioPlayerManager.playbackState

    init {
        observeAudio()
    }

    private fun observeAudio() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAudioRealtime().collect { list ->
                val sourceList = if (list.isEmpty()) getMockAudio() else list
                
                // Filter by artistId if present
                _audioList.value = if (artistId != null) {
                    sourceList.filter { it.artistIds.contains(artistId) }
                } else {
                    sourceList
                }
                
                _isLoading.value = false
            }
        }
    }

    private fun getMockAudio(): List<Audio> {
        return listOf(
            Audio(
                audioId = "m1",
                title = "Bheeshma Vijaya - Talamaddale",
                category = "Classic Talamaddale",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                artistIds = listOf("b1", "a1"),
                artistName = "Kalinga Navada",
                isFeatured = true
            ),
            Audio(
                audioId = "m2",
                title = "Vali Vadhe - Arthadhari Special",
                category = "Traditional",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                artistIds = listOf("a2", "b3"),
                artistName = "Sheni Gopalakrishna Bhat"
            ),
            Audio(
                audioId = "m3",
                title = "Karna Parva - Emotional Dialogue",
                category = "Classic Talamaddale",
                audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                artistIds = listOf("b1", "a4"),
                artistName = "Chittani Ramachandra Hegde"
            )
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addAudio(audio: Audio) {
        viewModelScope.launch {
            repository.addAudio(audio)
        }
    }

    fun deleteAudio(audioId: String) {
        viewModelScope.launch {
            repository.deleteAudio(audioId)
        }
    }

    fun playAudio(audio: Audio) {
        audioPlayerManager.play(audio)
    }

    fun togglePlayback() {
        audioPlayerManager.togglePlay()
    }
    
    fun stopPlayback() {
        audioPlayerManager.stop()
    }
}
