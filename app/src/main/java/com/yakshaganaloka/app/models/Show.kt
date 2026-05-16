package com.yakshaganaloka.app.models

data class Show(
    val id: String,
    val melaName: String,
    val location: String, // City or general area
    val venue: String, // Specific venue
    val date: String,
    val time: String,
    val isTonight: Boolean = false
)

object DummyData {
    val shows = listOf(
        Show("1", "Saligrama Mela", "Udupi", "Sri Krishna Mutt Grounds", "May 4, 2026", "7:00 PM", isTonight = true),
        Show("2", "Perdoor Mela", "Kundapura", "Kodi Beach Open Air Theatre", "May 4, 2026", "8:30 PM", isTonight = true),
        Show("3", "Mandarthi Mela", "Brahmavara", "Mandarthi Temple Premises", "May 5, 2026", "6:30 PM"),
        Show("4", "Kamalashile Mela", "Mangaluru", "Town Hall", "May 6, 2026", "7:00 PM"),
        Show("5", "Dharmasthala Mela", "Dharmasthala", "Amrutha Varshini Hall", "May 8, 2026", "6:00 PM"),
        Show("6", "Surathkal Mela", "Surathkal", "Govinda Dasa College Grounds", "May 10, 2026", "9:00 PM")
    )
}
