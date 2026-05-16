package com.yakshaganaloka.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yakshaganaloka.app.models.YakshaganaEvent

@Composable
fun EditEventDialog(
    event: YakshaganaEvent,
    onDismiss: () -> Unit,
    onSave: (YakshaganaEvent) -> Unit
) {
    var title by remember { mutableStateOf(event.title) }
    var melaName by remember { mutableStateOf(event.melaName) }
    var locationName by remember { mutableStateOf(event.location) }
    var date by remember { mutableStateOf(event.date) }
    var time by remember { mutableStateOf(event.time) }
    var lat by remember { mutableStateOf(event.latitude.toString()) }
    var lng by remember { mutableStateOf(event.longitude.toString()) }
    var description by remember { mutableStateOf(event.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Event") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = melaName, onValueChange = { melaName = it }, label = { Text("Mela Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = locationName, onValueChange = { locationName = it }, label = { Text("Venue Location") }, modifier = Modifier.fillMaxWidth())
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 9:30 PM)") }, modifier = Modifier.weight(1f))
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Longitude") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(event.copy(
                    title = title,
                    melaName = melaName,
                    location = locationName,
                    date = date,
                    time = time,
                    latitude = lat.toDoubleOrNull() ?: event.latitude,
                    longitude = lng.toDoubleOrNull() ?: event.longitude,
                    description = description
                ))
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
