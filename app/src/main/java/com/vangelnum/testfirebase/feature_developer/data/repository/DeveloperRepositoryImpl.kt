package com.vangelnum.testfirebase.feature_developer.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vangelnum.testfirebase.common.Resource
import com.vangelnum.testfirebase.feature_developer.domain.model.UserPhotos
import com.vangelnum.testfirebase.feature_developer.domain.repository.DeveloperRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DeveloperRepositoryImpl : DeveloperRepository {
    override suspend fun getUsersPhotos(): Flow<Resource<List<UserPhotos>>> = callbackFlow {
        Resource.Loading(data = null)
        val personCollection =
            Firebase.firestore.collection("users").addSnapshotListener { snapshot, error ->
                val response = snapshot?.let {
                    val userPhotos = it.toObjects<UserPhotos>()
                    Resource.Success(data = userPhotos)
                }
                error.let {
                    Resource.Error(message = it?.message.toString(), data = null)
                }
                if (response != null) {
                    trySend(response).isSuccess
                }

            }
        awaitClose {
            personCollection.remove()
        }
    }

    override suspend fun addUsersPhotosFromDeveloper(
        onePhoto: String,
        collectPhotos: UserPhotos,
    ) {

        val myCollection =
            Firebase.firestore.collection("images")
                .document("tutor")
        val mapUpdate = mapOf(
            "arrayImages" to FieldValue.arrayUnion(onePhoto)
        )
        myCollection.update(mapUpdate).await()

        val personCollection = Firebase.firestore.collection("users").document(collectPhotos.userId)
        personCollection.update(mapOf(
            "url" to FieldValue.arrayRemove(onePhoto)
        )).await()

    }

    override suspend fun deleteUsersPhotosFromDeveleoper(
        onePhoto: String,
        collectPhotos: UserPhotos,
    ) {

        val personCollection =
            Firebase.firestore.collection("users")
                .document(collectPhotos.userId)
        personCollection.update(mapOf(
            "url" to FieldValue.arrayRemove(onePhoto)
        ))

    }
}