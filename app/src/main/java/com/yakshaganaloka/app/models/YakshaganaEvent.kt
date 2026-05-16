package com.yakshaganaloka.app.models

import com.google.android.gms.maps.model.LatLng

/**
 * Data model for a Yakshagana performance event.
 */
data class YakshaganaEvent(
    val id: String = "",
    val conductorId: String = "",
    val melaName: String = "",
    val title: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "", // e.g., 09:30 PM
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = ""
) {
    val position: LatLng
        get() = LatLng(latitude, longitude)
}
