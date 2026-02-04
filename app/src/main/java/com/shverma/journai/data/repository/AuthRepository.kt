package com.shverma.journai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.shverma.journai.data.network.ApiService
import com.shverma.journai.data.network.model.UserResponse
import com.shverma.journai.data.preference.DataStoreHelper
import com.shverma.journai.utils.Resource
import kotlinx.coroutines.tasks.await
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Resource<UserResponse>
}

class AuthRepositoryImpl @Inject constructor(
    private val journAiDataStore: DataStoreHelper,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithGoogle(
        idToken: String
    ): Resource<UserResponse> {
        return try {
            val credential =
                GoogleAuthProvider.getCredential(
                    idToken,
                    null
                )

            val result =
                firebaseAuth
                    .signInWithCredential(credential)
                    .await()

            val user = result.user
                ?: return Resource.Error("User is null")


            val token =
                user.getIdToken(false).await().token
                    ?: ""


            val response = UserResponse(
                id = user.uid,
                email = user.email.orEmpty(),
                created_at = OffsetDateTime.now(),
                access_token = token,
            )


            journAiDataStore.saveUserSession(
                accessToken = response.access_token,
                userId = response.id,
                userEmail = response.email
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

}
