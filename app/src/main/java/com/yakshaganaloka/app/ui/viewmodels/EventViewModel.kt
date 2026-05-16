package com.yakshaganaloka.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakshaganaloka.app.models.YakshaganaEvent
import com.yakshaganaloka.app.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {
    private val TAG = "EventViewModel"

    private val _liveEvents = MutableStateFlow<List<YakshaganaEvent>>(emptyList())
    val liveEvents: StateFlow<List<YakshaganaEvent>> = _liveEvents.asStateFlow()

    private val _upcomingEvents = MutableStateFlow<List<YakshaganaEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<YakshaganaEvent>> = _upcomingEvents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEventsRealtime()
                .catch { e ->
                    Log.e(TAG, "Firestore collection failed: ${e.message}")
                    _isLoading.value = false
                }
                .collect { allEvents ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val today = sdf.format(Date())
                    
                    val live = allEvents.filter { it.date == today }
                    val upcoming = allEvents.filter { it.date > today }.sortedBy { it.date }
                    
                    if (allEvents.isEmpty()) {
                        loadInitialMockData()
                    } else {
                        _liveEvents.value = live
                        _upcomingEvents.value = upcoming
                    }
                    
                    _isLoading.value = false
                }
        }
    }

    private fun loadInitialMockData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = sdf.format(Date())
        val mock = mutableListOf<YakshaganaEvent>()
        
        // Live Event
        mock.add(YakshaganaEvent(
            id = "m1", 
            melaName = "Saligrama Mela", 
            title = "Devi Mahatme", 
            location = "Udupi Krishna Temple", 
            date = today, 
            time = "9:30 PM",
            latitude = 13.3409, 
            longitude = 74.7421
        ))

        // Helper to get future dates
        fun getFutureDate(days: Int): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, days)
            return sdf.format(cal.time)
        }
        
        // Upcoming Events
        mock.add(YakshaganaEvent(
            id = "m2", 
            melaName = "Mandarthi Mela", 
            title = "Sudhanva Kalaga", 
            location = "Mandarthi Temple Ground", 
            date = getFutureDate(1), 
            time = "10:00 PM",
            latitude = 13.5417, 
            longitude = 74.8333
        ))

        mock.add(YakshaganaEvent(
            id = "m3", 
            melaName = "Dharmasthala Mela", 
            title = "Bheeshma Vijaya", 
            location = "Dharmasthala Exhibition Ground", 
            date = getFutureDate(2), 
            time = "9:45 PM",
            latitude = 12.9515, 
            longitude = 75.3907
        ))

        mock.add(YakshaganaEvent(
            id = "m4", 
            melaName = "Perdoor Mela", 
            title = "Daksha Yajna", 
            location = "Perdoor Market Yard", 
            date = getFutureDate(3), 
            time = "10:15 PM",
            latitude = 13.3934, 
            longitude = 74.8962
        ))

        mock.add(YakshaganaEvent(
            id = "m5", 
            melaName = "Kamalashile Mela", 
            title = "Gadhayuddha", 
            location = "Kamalashile Temple Premises", 
            date = getFutureDate(5), 
            time = "9:30 PM",
            latitude = 13.6333, 
            longitude = 74.9167
        ))

        mock.add(YakshaganaEvent(
            id = "m6", 
            melaName = "Kateel Mela", 
            title = "Mahishamardini", 
            location = "Kateel Temple Car Street", 
            date = getFutureDate(6), 
            time = "10:30 PM",
            latitude = 13.0185, 
            longitude = 74.8471
        ))

        mock.add(YakshaganaEvent(
            id = "m7", 
            melaName = "Hiriyadka Mela", 
            title = "Karna Parva", 
            location = "Hiriyadka School Ground", 
            date = getFutureDate(8), 
            time = "9:00 PM",
            latitude = 13.3461, 
            longitude = 74.8436
        ))

        mock.add(YakshaganaEvent(
            id = "m8", 
            melaName = "Halady Mela", 
            title = "Krishnarjuna Kalaga", 
            location = "Halady Junction", 
            date = getFutureDate(10), 
            time = "10:00 PM",
            latitude = 13.5658, 
            longitude = 74.8984
        ))

        mock.add(YakshaganaEvent(
            id = "m9", 
            melaName = "Sigandur Mela", 
            title = "Chowdeshwari Mahatme", 
            location = "Sigandur Ferry Point", 
            date = getFutureDate(12), 
            time = "9:30 PM",
            latitude = 14.1234, 
            longitude = 74.9567
        ))
        
        _liveEvents.value = mock.filter { it.date == today }
        _upcomingEvents.value = mock.filter { it.date > today }
    }

    fun addEvent(event: YakshaganaEvent) {
        viewModelScope.launch {
            repository.addEvent(event)
        }
    }

    fun updateEvent(event: YakshaganaEvent) {
        viewModelScope.launch {
            repository.updateEvent(event)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }
}
