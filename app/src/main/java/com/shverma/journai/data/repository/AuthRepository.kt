package com.shverma.journai.data.repository

import com.shverma.journai.data.network.ApiService
import com.shverma.journai.data.network.model.ChangePasswordRequest
import com.shverma.journai.data.network.model.LoginRequest
import com.shverma.journai.data.network.model.RegisterRequest
import com.shverma.journai.data.network.model.UserResponse
import com.shverma.journai.data.preference.DataStoreHelper
import com.shverma.journai.utils.Resource
import com.shverma.journai.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<UserResponse>
    suspend fun register(email: String, password: String): Resource<UserResponse>
    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val journAiDataStore: DataStoreHelper
) : AuthRepository {
    override suspend fun login(email: String, password: String): Resource<UserResponse> {
        val result = safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.login(LoginRequest(email, password)) }
        )
        if (result is Resource.Success && result.data != null) {
            journAiDataStore.saveUserSession(
                accessToken = result.data.access_token,
                tokenType = result.data.token_type,
                userId = result.data.id,
                userEmail = result.data.email,
            )
        }
        return result
    }

    override suspend fun register(email: String, password: String): Resource<UserResponse> {
        val result = safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { apiService.register(RegisterRequest(email, password)) }
        )
        if (result is Resource.Success && result.data != null) {
            journAiDataStore.saveUserSession(
                accessToken = result.data.access_token,
                tokenType = result.data.token_type,
                userId = result.data.id,
                userEmail = result.data.email,
            )
        }
        return result
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit> {
        return safeApiCall(
            dispatcher = Dispatchers.IO,
            apiCall = { 
                apiService.changePassword(
                    ChangePasswordRequest(
                        current_password = currentPassword,
                        new_password = newPassword
                    )
                ) 
            }
        )
    }
}
