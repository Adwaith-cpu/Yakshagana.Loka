package com.yakshaganaloka.app.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(private val storage: FirebaseStorage) {

    suspend fun uploadArtistImage(artistId: String, imageUri: Uri): String {
        val ref = storage.reference.child("artists/$artistId.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        val ref = storage.reference.child("profiles/$userId.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }
}
