package com.yakshaganaloka.app.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.yakshaganaloka.app.models.Artist
import com.yakshaganaloka.app.models.Audio
import com.yakshaganaloka.app.models.YakshaganaEvent
import com.yakshaganaloka.app.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val TAG = "FirestoreRepository"

    // --- Artist Module ---

    suspend fun addArtist(artist: Artist) {
        val id = artist.id.ifEmpty { UUID.randomUUID().toString() }
        db.collection("artists").document(id).set(artist.copy(id = id)).await()
    }

    suspend fun getAllArtists(): List<Artist> {
        return db.collection("artists").get().await().documents.mapNotNull { it.toObject<Artist>() }
    }

    fun getArtistsRealtime(): Flow<List<Artist>> = callbackFlow {
        val subscription = db.collection("artists").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.documents.mapNotNull { it.toObject<Artist>() })
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateArtist(artist: Artist) {
        db.collection("artists").document(artist.id).set(artist).await()
    }

    suspend fun deleteArtist(artistId: String) {
        db.collection("artists").document(artistId).delete().await()
    }

    // --- Event Module ---

    suspend fun addEvent(event: YakshaganaEvent) {
        val id = event.id.ifEmpty { UUID.randomUUID().toString() }
        db.collection("events").document(id).set(event.copy(id = id)).await()
    }

    suspend fun updateEvent(event: YakshaganaEvent) {
        db.collection("events").document(event.id).set(event).await()
    }

    suspend fun deleteEvent(eventId: String) {
        db.collection("events").document(eventId).delete().await()
    }

    fun getEventsRealtime(): Flow<List<YakshaganaEvent>> = callbackFlow {
        val subscription = db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.mapNotNull { it.toObject<YakshaganaEvent>() })
                }
            }
        awaitClose { subscription.remove() }
    }

    // --- Audio Module ---

    suspend fun addAudio(audio: Audio) {
        val id = audio.audioId.ifEmpty { UUID.randomUUID().toString() }
        db.collection("audio").document(id).set(audio.copy(audioId = id)).await()
    }

    suspend fun deleteAudio(audioId: String) {
        db.collection("audio").document(audioId).delete().await()
    }

    fun getAudioRealtime(): Flow<List<Audio>> = callbackFlow {
        val subscription = db.collection("audio").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.documents.mapNotNull { it.toObject<Audio>() })
            }
        }
        awaitClose { subscription.remove() }
    }

    // --- User Module ---

    suspend fun saveUserProfile(user: User) {
        if (user.userId.isEmpty()) {
            Log.e(TAG, "Attempted to save user profile with empty UID")
            return
        }
        Log.d(TAG, "Saving profile for UID: ${user.userId}")
        db.collection("users").document(user.userId).set(user).await()
    }

    suspend fun getUserProfile(userId: String): User? {
        if (userId.isEmpty()) return null
        return db.collection("users").document(userId).get().await().toObject<User>()
    }

    fun getUserProfileFlow(userId: String): Flow<User?> = callbackFlow {
        if (userId.isEmpty()) {
            Log.w(TAG, "getUserProfileFlow called with empty UID")
            trySend(null)
            return@callbackFlow
        }
        
        Log.d(TAG, "Listening to profile updates for UID: $userId")
        val subscription = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error in user profile listener for $userId", error)
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<User>())
            }
        awaitClose { 
            Log.d(TAG, "Removing profile listener for UID: $userId")
            subscription.remove() 
        }
    }
}
