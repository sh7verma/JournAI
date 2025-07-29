package com.shverma.app.data.network.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: String,
    val email: String,
    val created_at: String,
    val access_token: String,
    val token_type: String // "bearer"
)
