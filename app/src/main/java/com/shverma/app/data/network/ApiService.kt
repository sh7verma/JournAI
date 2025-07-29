package com.shverma.app.data.network

import com.shverma.app.data.network.model.JournalByDateResponse
import com.shverma.app.data.network.model.JournalDetail
import com.shverma.app.data.network.model.JournalEntryCreate
import com.shverma.app.data.network.model.LoginRequest
import com.shverma.app.data.network.model.RegisterRequest
import com.shverma.app.data.network.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    // --- Journal endpoints ---
    @POST("/journal/")
    suspend fun createJournalEntry(@Body entry: JournalEntryCreate): Response<JournalDetail>

    @GET("/journal/")
    suspend fun getJournalEntry(@Query("id") id: String): Response<JournalDetail>

    @GET("/journal/history")
    suspend fun getJournalHistory(): Response<List<JournalDetail>>

    @PUT("/journal/{id}")
    suspend fun updateJournalEntry(
        @Path("id") id: String,
        @Body entry: JournalEntryCreate
    ): Response<JournalDetail>

    @DELETE("/journal/{id}")
    suspend fun deleteJournalEntry(@Path("id") id: String): Response<Unit>

    @GET("/journal/by-date")
    suspend fun getJournalEntriesByDate(
        @Query("date") date: String
    ): Response<JournalByDateResponse>
}