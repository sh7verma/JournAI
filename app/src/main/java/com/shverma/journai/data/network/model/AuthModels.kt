package com.shverma.journai.data.network.model

import org.threeten.bp.OffsetDateTime

data class UserResponse(
    val id: String,
    val email: String,
    val created_at: OffsetDateTime,
    val access_token: String,
)