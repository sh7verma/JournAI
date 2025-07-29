package com.shverma.app.data.network.model

import org.threeten.bp.OffsetDateTime

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
    val created_at: OffsetDateTime,
    val access_token: String,
    val token_type: String // "bearer"
)
