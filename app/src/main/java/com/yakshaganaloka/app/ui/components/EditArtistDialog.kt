package com.yakshaganaloka.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yakshaganaloka.app.models.Artist

@Composable
fun EditArtistDialog(
    artist: Artist,
    onDismiss: () -> Unit,
    onSave: (Artist) -> Unit
) {
    var name by remember { mutableStateOf(artist.name) }
    var role by remember { mutableStateOf(artist.role) }
    var vesha by remember { mutableStateOf(artist.vesha) }
    var description by remember { mutableStateOf(artist.description) }
    var imageUrl by remember { mutableStateOf(artist.imageUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Artist") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vesha,
                    onValueChange = { vesha = it },
                    label = { Text("Iconic Vesha") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                onSave(artist.copy(
                    name = name,
                    role = role,
                    vesha = vesha,
                    description = description,
                    imageUrl = imageUrl.ifBlank { null }
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
