package com.yakshaganaloka.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakshaganaloka.app.models.YakshaganaEvent
import com.yakshaganaloka.app.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<YakshaganaEvent>>(emptyList())
    val events: StateFlow<List<YakshaganaEvent>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedEvent = MutableStateFlow<YakshaganaEvent?>(null)
    val selectedEvent: StateFlow<YakshaganaEvent?> = _selectedEvent.asStateFlow()

    init {
        observeEvents()
    }

    fun selectEvent(event: YakshaganaEvent?) {
        _selectedEvent.value = event
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEventsRealtime().collect { list ->
                _events.value = if (list.isEmpty()) getMockEvents() else list
                _isLoading.value = false
            }
        }
    }

    private fun getMockEvents(): List<YakshaganaEvent> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val today = sdf.format(calendar.time)
        
        return listOf(
            YakshaganaEvent(
                id = "mock1",
                melaName = "Saligrama Mela",
                title = "Devi Mahatme",
                location = "Udupi Krishna Temple",
                date = today,
                latitude = 13.3409,
                longitude = 74.7421
            ),
            YakshaganaEvent(
                id = "mock2",
                melaName = "Dharmasthala Mela",
                title = "Bheeshma Vijaya",
                location = "Mangalore Town Hall",
                date = today,
                latitude = 12.8701,
                longitude = 74.8804
            )
        )
    }
}
